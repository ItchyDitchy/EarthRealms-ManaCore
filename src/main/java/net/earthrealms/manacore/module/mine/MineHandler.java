package net.earthrealms.manacore.module.mine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.sk89q.worldedit.function.pattern.Pattern;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.object.Account;
import net.earthrealms.manacore.module.mine.command.MineCommand;
import net.earthrealms.manacore.module.mine.object.Mine;

public class MineHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	private Map<UUID, Mine> mines = new HashMap<UUID, Mine>();
	private Map<Integer, Pattern> blocks = new HashMap<Integer, Pattern>();
	
	// Settings
	private int minHeight = 20;
	private int maxHeight = 79;
	
	public MineHandler() {
		new MineCommand();
	}
	
	public void reload() {
		
	}
	
	public void createMine(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
        SuperiorSkyblockAPI.getGrid().createIsland(SuperiorSkyblockAPI.getPlayer(player), SuperiorSkyblockAPI.getSuperiorSkyblock().getSchematics().getSchematics().get(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), Biome.PLAINS,
                player.toString(), false);
	}
	
	public void resetMine(UUID uuid) {
		
	}
	
	public Pattern getBlocks(UUID uuid) {
		Account account = plugin.getAccountHandler().getAccount(uuid);
		int rank = account.getRank();
		
		Pattern pattern = blocks.get(rank);
		
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
	
	public int getMinHeight() {
		return minHeight;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
}
