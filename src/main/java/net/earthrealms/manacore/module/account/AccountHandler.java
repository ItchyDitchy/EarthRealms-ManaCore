package net.earthrealms.manacore.module.account;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.listener.OnlinePlayerListener;
import net.earthrealms.manacore.module.account.object.Account;
import net.earthrealms.manacore.module.mine.listener.PlayerListener;

public class AccountHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	private Map<UUID, Account> accounts;
	private Map<String, UUID> nameIDs;
	
	public AccountHandler() {
		if (Bukkit.getServer().getOnlineMode()) {
			new OnlinePlayerListener();
		} else {
			new PlayerListener();
		}
	}
	
	public void loadAccounts() {
		
		accounts = new HashMap<UUID, Account>();
		nameIDs = new HashMap<String, UUID>();
		
        final File folder = new File(plugin.getDataFolder(), "profiles");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        String[] fAccounts = folder.list();
        
		for (int index = 0; index < fAccounts.length; index++) {
			String fileName = fAccounts[index];
			File file = new File(folder, fileName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			Account account = new Account();
			
			// Economy
			account.setUntradableCoins(config.getDouble("untradableCoins"));
			account.setCredits(config.getDouble("credits"));
			account.setPremiumCoins(config.getDouble("premiumCoins"));
			account.setShards(config.getDouble("shards"));
			
			// Status
			account.setDisplayName(config.getString("displayName"));
			account.setPremium(config.getBoolean("premium"));
			account.setOperator(config.getBoolean("operator"));
			
			account.setPrefix(config.getString("prefix"));
			account.setTitle(config.getString("title"));
			account.setSuffix(config.getString("suffix"));
			account.setNickColor(config.getString("nickColor"));
			account.setChatColor(config.getString("chatColor"));
			
			// Skills
			account.setGrindingSkillLevel(config.getInt("grindingSkillLevel"));
			account.setGrindingSkillXP(config.getInt("grindingSkillXP"));
			account.setMiningSkillLevel(config.getInt("miningSkillLevel"));
			account.setMiningSkillXP(config.getInt("miningSkillXP"));
			account.setFarmingSkillLevel(config.getInt("farmingSkillLevel"));
			account.setFarmingSkillXP(config.getInt("farmingSkillXP"));
			
			// Games
			account.setBarWins(config.getInt("barWins"));
			account.setBarLosses(config.getInt("barLosses"));
			account.setCoinFlipWins(config.getInt("coinFlipWins"));
			account.setCoinFlipLosses(config.getInt("coinFlipLosses"));
			account.setLotteryWins(config.getInt("lotteryWins"));
			account.setLotteryLosses(config.getInt("lotteryLosses"));
			account.setScratchOffWins(config.getInt("scratchOffWins"));
			account.setScratchOffLosses(config.getInt("scratchOffLosses"));
			account.setChatGameWins(config.getInt("chatGameWins"));
			account.setChatGameLosses(config.getInt("chatGameLosses"));
			
			// Maps & Lists
			Map<String, Long> cooldowns = new HashMap<String, Long>();
			if (config.isSet("cooldowns") && config.isConfigurationSection("cooldowns")) {
				for (String id : config.getConfigurationSection("cooldowns").getKeys(false)) {
					cooldowns.put(id, config.getLong("cooldowns." + id));
				}
			}
			Map<Integer, ItemStack[]> backpacks = new HashMap<Integer, ItemStack[]>();
			if (config.isSet("backpacks") && config.isConfigurationSection("backpacks")) {
				for (int backpackNumber : account.getBackpacks().keySet()) {
					backpacks.put(backpackNumber, ((List<ItemStack>) config.getList("backpacks." + backpackNumber)).toArray(new ItemStack[0]));
				}
			}
			account.setCooldowns(cooldowns);
			account.setBackpacks(backpacks);
			account.setTitles(config.getStringList("titles"));
			
			UUID uuid = UUID.fromString(fileName.replaceFirst(".yml", ""));
			saveAccount(uuid, account);
		}
	}
	
	public void saveAccounts() {
        final File folder = new File(plugin.getDataFolder(), "profiles");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        Long time = System.currentTimeMillis();
        plugin.getLogger().info("Saving " + accounts.keySet().size() + " accounts.");
        
        if (accounts.keySet().size() == 0) {
        	return;
        }
        
		for (UUID uuid : accounts.keySet()) {
			Account account = accounts.get(uuid);
			
			File file = new File(folder, uuid.toString() + ".yml");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			// Economy
			config.set("untradableCoins", account.getUntradableCoins());
			config.set("credits", account.getCredits());
			config.set("premiumCoins", account.getPremiumCoins());
			config.set("shards", account.getShards());
			
			// Status
			config.set("displayName", account.getDisplayName());
			config.set("premium", account.isPremium());
			config.set("operator", account.isOperator());
			config.set("prefix", account.getPrefix());
			config.set("title", account.getTitle());
			config.set("suffix", account.getSuffix());
			config.set("nickColor", account.getNickColor());
			config.set("chatColor", account.getChatColor());
			
			// Skills
			config.set("grindingSkillLevel", account.getGrindingSkillLevel());
			config.set("grindingSkillXP", account.getGrindingSkillXP());
			config.set("miningSkillLevel", account.getMiningSkillLevel());
			config.set("miningSkillXP", account.getMiningSkillXP());
			config.set("farmingSkillLevel", account.getFarmingSkillLevel());
			config.set("farmingSkillXP", account.getFarmingSkillXP());
			
			// Games
			config.set("barWins", account.getBarWins());
			config.set("barLosses", account.getBarWins());
			config.set("coinFlipWins", account.getCoinFlipWins());
			config.set("coinFlipLosses", account.getCoinFlipWins());
			config.set("lotteryWins", account.getLotteryWins());
			config.set("lotteryLosses", account.getLotteryLosses());
			config.set("scratchOffWins", account.getScratchOffWins());
			config.set("scratchOffLosses", account.getScratchOffWins());
			config.set("chatGameWins", account.getChatGameWins());
			config.set("chatGameLosses", account.getChatGameWins());
			
			// Maps & Lists
			config.set("cooldowns", null);
			for (String id : account.getCooldowns().keySet()) {
				config.set("cooldowns." + id, account.getCooldowns().get(id));
			}
			config.set("backpacks", null);
			for (int backpackNumber : account.getBackpacks().keySet()) {
				config.set("backpacks." + backpackNumber, account.getBackpacks().get(backpackNumber));
			}
			config.set("titles", account.getTitles());
			
			try {
				config.save(file);
			} catch (IOException exception) {
				 exception.printStackTrace();
			}
		}
		plugin.getLogger().info("It took " + (System.currentTimeMillis() - time) + "ms to save " + accounts.keySet().size() + " accounts.");
	}
	
	public Account getAccount(UUID uuid) {
		return accounts.get(uuid);
	}
	
	public Map<UUID, Account> getAccounts() {
		return accounts;
	}
	
	public void saveAccount(UUID uuid, Account account) {
		accounts.put(uuid, account);
		nameIDs.put(account.getDisplayName().toUpperCase(), uuid);
	}
	
	public UUID getUUID(String name) {
		name = name.toUpperCase();
		if (nameIDs.containsKey(name)) {
			return nameIDs.get(name);
		}
		return null;
	}
}