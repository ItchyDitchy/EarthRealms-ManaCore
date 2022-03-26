package net.earthrealms.manacore.module.mine;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.bgsoftware.common.config.CommentedConfiguration;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.object.Account;
import net.earthrealms.manacore.module.mine.command.MineCommand;
import net.earthrealms.manacore.module.mine.object.Mine;
import net.earthrealms.manacore.module.mine.object.RegenDirection;

public class MineHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	private Map<UUID, Mine> mines = new HashMap<UUID, Mine>();
	private Map<Integer, Pattern> blocks = new HashMap<Integer, Pattern>();
	
	// Settings
	private RegenDirection defaultRegenDirection;
	private Material outlineBlock;
	private int outlineOffsetEdge;
	private int outlineOffsetBottom;
	private int sizeVerticalMinimum;
	private int sizeVerticalMaximum;
	private int sizeHorizontalMinimum;
	private int sizeHorizontalMaximum;
	private int taxesDefault;
	private Map<String, Integer> taxesMaximum;
	
	public MineHandler() {
		new MineCommand();
	}
	
	public void reload() {
		File mineFolder = new File(plugin.getDataFolder(), "modules/mine");
        if (!mineFolder.exists()) {
        	plugin.saveResource("modules/mine/config.yml", false);
        }
        File configFile = new File(mineFolder, "config.yml");
        CommentedConfiguration config = CommentedConfiguration.loadConfiguration(configFile);

        defaultRegenDirection = RegenDirection.valueOf(config.getString("block_generation", "up").toUpperCase());
        
        outlineBlock = Material.valueOf(config.getString("outline.block", "bedrock").toUpperCase());
        outlineOffsetEdge = config.getInt("outline.offset.edge");
        outlineOffsetBottom = config.getInt("outline.offset.bottom");
        
        ConfigurationSection minesSection = config.getConfigurationSection("mines");
        for (String mineID : minesSection.getKeys(false)) {
        	try {
        		int mineLevel = Integer.valueOf(mineID);
        		RandomPattern pattern = new RandomPattern();
        		for (String blockKey : minesSection.getConfigurationSection(mineID + ".blocks").getKeys(false)) {
        			pattern.add(BukkitAdapter.adapt(Material.getMaterial(blockKey.toUpperCase()).createBlockData()), minesSection.getDouble(mineID + ".blocks." + blockKey));
        		}
        		blocks.put(mineLevel, pattern);
        	} catch (Exception e) {
        		plugin.log("Did not register mine blocks " + mineID + " for there is an exception.");
        	}
        }
        
        sizeVerticalMinimum = config.getInt("size.vertical.minimum", 20);
        sizeVerticalMaximum = config.getInt("size.vertical.maximum", 79);
        sizeHorizontalMinimum = config.getInt("size.vertical.minimum", 9);
        sizeHorizontalMaximum = config.getInt("size.vertical.maximum", 177);
        
        taxesDefault = config.getInt("taxes.default", 1);
        taxesMaximum = new HashMap<String, Integer>();
        for (String key : config.getConfigurationSection("taxes.maximum").getKeys(false)) {
        	taxesMaximum.put(key.toUpperCase(), config.getInt("taxes.maximum." + key, 1));
        }
	}
	
	public void createMine(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
        SuperiorSkyblockAPI.getGrid().createIsland(SuperiorSkyblockAPI.getPlayer(player), SuperiorSkyblockAPI.getSuperiorSkyblock().getSchematics().getSchematics().get(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), Biome.PLAINS,
                player.toString(), false);
	}
	
	public void resetMine(UUID uuid) {
		
	}
	
	public Pattern getBlocks(UUID uuid) {
		@SuppressWarnings("unused")
		Account account = plugin.getAccountHandler().getAccount(uuid);
		int rank = 1; // TODO account.getRank();
		
		Pattern pattern = blocks.get(rank);
		
		ManaCorePlugin.log(blocks.toString());
		if (pattern == null) {
			while(pattern == null) {
				rank--;
				pattern = blocks.get(rank);
			}
			blocks.put(rank, pattern);
		}
		
		return pattern;
	}
	
	public Pattern getBlocks(SuperiorPlayer superiorPlayer) {
		return getBlocks(superiorPlayer.getUniqueId());
	}
	
	public RegenDirection getDefaultRegenDirection() {
		return defaultRegenDirection;
	}
	
	public Material getOutlineBlock() {
		return outlineBlock;
	}
	
	public int getOutlineOffsetEdge() {
		return outlineOffsetEdge;
	}
	
	public int getoutlineOffsetBottom() {
		return outlineOffsetBottom;
	}
	
	public int getSizeVerticalMinimum() {
		return sizeVerticalMinimum;
	}
	
	public int getSizeVerticalMaximum() {
		return sizeVerticalMaximum;
	}
	
	public int getSizeHorizontalMinimum() {
		return sizeHorizontalMinimum;
	}
	
	public int getSizeHorizontalMaximum() {
		return sizeHorizontalMaximum;
	}
	
	public int getTaxesDefault() {
		return taxesDefault;
	}
	
	public Map<String, Integer> getTaxesMaximum() {
		return taxesMaximum;
	}
}
