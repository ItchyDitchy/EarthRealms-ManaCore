package net.earthrealms.manacore.module.mine.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.lang.Message;

public class Mine {

	private UUID mineID;
	private UUID owner;
	private UUID coOwner;
	
	private List<UUID> whitelist;
	private List<UUID> blacklist;
	private List<UUID> priorities;
	
	private int currentBlocks = 0;
	private int maxBlocks = 0;
	
	private int mineSize = 9;
	private boolean open = true;
	
	private int maxMiners = 0;
	private List<UUID> miners = new ArrayList<UUID>();
	
	private long resetCooldown = System.currentTimeMillis();
	
	private Location spawn;
	
	/**
	 * @param uuid The UUID of the mine's owner.
	 */
	public Mine(@Nonnull UUID uuid) {
		this.mineID = uuid;
		this.mineSize = 9;
	}
	
	/**
	 * @param uuid The UUID of the mine's owner.
	 * @param size The size of the mine.
	 */
	public Mine(@Nonnull UUID uuid, @Nonnull int size) {
		this.mineID = uuid;
		this.mineSize = size;
	}

	/**
	 * @param uuid The UUID of the player to teleport.
	 */
	public void teleport(@Nonnull UUID uuid) {
		teleport(Bukkit.getPlayer(uuid));
	}
	
	/**
	 * @param player The player to teleport.
	 */
	public void teleport(@Nonnull Player player) {
		player.teleport(getSpawn());
	}
	
	/**
	 * @param superiorPlayer The superior player to teleport.
	 */
	public void teleport(@Nonnull SuperiorPlayer superiorPlayer) {
		teleport(superiorPlayer.asPlayer());
	}
	
	public boolean canTeleport(@Nonnull Player player) {

		// TODO recode
		
		if (blacklist.contains(player.getUniqueId())) {
			return false;
		}
		if (!open && !whitelist.contains(player.getUniqueId())) {
			return false;
		}
		if (miners.contains(player.getUniqueId())) {
			return true;
		}
		if (miners.size() >= maxMiners) {
			if (isOwner(player) || isCoOwner(player)) {
				List<Player> priority = new ArrayList<Player>();
				List<Player> random = new ArrayList<Player>();
				
				for (UUID uuid : miners) {
					Player miner = Bukkit.getPlayer(uuid);
					if (miner == null) {
						miners.remove(uuid);
						miners.add(player.getUniqueId());
						return true;
					}
					if (isPrioritized(miner)) {
						priority.add(miner);
						continue;
					}
				}
				return false;
			}
			if (isPrioritized(player.getUniqueId())) {
				return false;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Reset the mine using the default regen setting.
	 */
	public void reset() {
		reset(RegenDirection.UP);
	}

	/**
	 * @param direction The direction of the mine will regenerate.
	 */
	public void reset(@Nonnull RegenDirection direction) {
		SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(mineID);
		Island island = superiorPlayer.getIsland();

		if (island == null) {
			return;
		}

		int halfSize = (mineSize-1)/2;
		
		Location center = island.getCenter(Environment.NORMAL);

		ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
		int delay = 0;

		int x1 = (int) (center.getX() - halfSize);
		int x2 = (int) (center.getX() + halfSize);
		int y1 = plugin.getMineHandler().getSizeVerticalMinimum();
		int y2 = plugin.getMineHandler().getSizeVerticalMaximum();
		int z1 = (int) (center.getZ() - halfSize) - 1;
		int z2 = (int) (center.getZ() + halfSize) - 1;
		
		Pattern pattern = ManaCorePlugin.getPlugin().getMineHandler().getBlocks(superiorPlayer);
		
		switch (direction) {
		case INSTANT:
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
			Region region = new CuboidRegion(
					BlockVector3.at(x1, y1, z1),
					BlockVector3.at(x2, y2, z2));

			editSession.setBlocks(region, pattern);
			editSession.close();
			break;
		case UP:
			for (int i = y1; i <= y2; i++) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
						Region region = new CuboidRegion(
								BlockVector3.at(x1, layer, z1),
								BlockVector3.at(x2, layer, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case DOWN:
			for (int i = y2; i >= y1; i--) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
						Region region = new CuboidRegion(
								BlockVector3.at(x1, layer, z1),
								BlockVector3.at(x2, layer, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case NORTH:
			for (int i = z2; i <= z1; i--) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
						Region region = new CuboidRegion(
								BlockVector3.at(x1, y1, layer),
								BlockVector3.at(x2, y2, layer));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case EAST:
			for (int i = x1; i <= x2; i++) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
						Region region = new CuboidRegion(
								BlockVector3.at(layer, y1, z1),
								BlockVector3.at(layer, y2, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case SOUTH:
			for (int i = z1; i <= z2; i++) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
						Region region = new CuboidRegion(
								BlockVector3.at(x1, y1, layer),
								BlockVector3.at(x2, y2, layer));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case WEST:
			for (int i = x2; i <= x1; i--) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
						Region region = new CuboidRegion(
								BlockVector3.at(layer, y1, z1),
								BlockVector3.at(layer, y2, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		}
	}
	
	public void setBlocks(@Nonnull Map<Material, Double> blocks) {

	}

	public void clearBlocks() {

	}

	public void generateBlocks() {

	}

	public void generateOutline() {
		SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(mineID);
		Island island = superiorPlayer.getIsland();


		int halfSize = ((mineSize-1)/2) + 2;
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
		Location center = island.getCenter(Environment.NORMAL);
		Region region = new CuboidRegion(BlockVector3.at(center.getX() + halfSize, 20, center.getZ() + halfSize), BlockVector3.at(center.getX() - halfSize, 79, center.getZ() - halfSize));

		RandomPattern pattern = new RandomPattern();

		pattern.add(BukkitAdapter.adapt(Material.BEDROCK.createBlockData()), 1);
		editSession.makeWalls(region, pattern);
		editSession.close();
	}

	public void generateDefaultMine() {

	}

	public void generateSchematic() {

	}

	/**
	 * @return the player instance of the mine owner.
	 */
	public Player getOwner() {
		return Bukkit.getPlayer(owner);
	}

	/**
	 * @return the uuid of the mine owner.
	 */
	public UUID getOwnerID() {
		return owner;
	}
	
	/**
	 * @return the offline instance of the mine owner.
	 */
	public OfflinePlayer getOfflineOwner() {
		return Bukkit.getOfflinePlayer(owner);
	}
	
	/**
	 * @return an player instance of the coowner.
	 */
	public Player getCoOwner() {
		return Bukkit.getPlayer(coOwner);
	}
	
	/**
	 * @return the uuid of the coowner.
	 */
	public UUID getCoOwnerID() {
		return coOwner;
	}
	
	/**
	 * @return an offline player instance of the coowner.
	 */
	public OfflinePlayer getOfflineCoOwner() {
		return Bukkit.getOfflinePlayer(coOwner);
	}
	
	// TODO description
	
	/**
	 * @param uuid
	 * @return
	 */
	public boolean isOwner(UUID uuid) {
		return owner == uuid;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public boolean isOwner(Player player) {
		return isOwner(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer
	 * @return
	 */
	public boolean isOwner(OfflinePlayer offlinePlayer) {
		return isOwner(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @param uuid
	 * @return
	 */
	public boolean isCoOwner(UUID uuid) {
		return coOwner == uuid;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public boolean isCoOwner(Player player) {
		return isCoOwner(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer
	 * @return
	 */
	public boolean isCoOwner(OfflinePlayer offlinePlayer) {
		return isCoOwner(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @return a list of whitelisted players' ids.
	 */
	public List<UUID> getWhitelistIDs() {
		return whitelist;
		
	}
	
	/**
	 * @return a list of whitelisted players.
	 */
	public List<Player> getWhitelist() {
		List<Player> whitelistList = new ArrayList();
		for (UUID uuid : whitelist) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				whitelistList.add(player);
			}
		}
		return whitelistList;
	}
	
	/**
	 * @param uuid The uuid of the player to whitelist.
	 */
	public void whitelist(@Nonnull UUID uuid) {
		if (whitelist.contains(uuid)) {
			return;
		}
		whitelist.add(uuid);
	}
	
	/**
	 * @param player The player to whitelist.
	 */
	public void whitelist(@Nonnull Player player) {
		whitelist(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer The offline player to whitelist.
	 */
	public void whitelist(@Nonnull OfflinePlayer offlinePlayer) {
		whitelist(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @param uuid The uuid of the player to unwhitelist from the mine.
	 */
	public void unwhitelist(@Nonnull UUID uuid) {
		if (!whitelist.contains(uuid)) {
			return;
		}
		whitelist.remove(uuid);
	}
	
	/**
	 * @param player The player to unwhitelist from the mine.
	 */
	public void unwhitelist(@Nonnull Player player) {
		unwhitelist(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer The offline player to unwhitelist from the mine.
	 */
	public void unwhitelist(@Nonnull OfflinePlayer offlinePlayer) {
		unwhitelist(offlinePlayer.getUniqueId());
	}

	// TODO documentation
	/**
	 * @param uuid
	 * @return
	 */
	public boolean isWhitelisted(UUID uuid) {
		return whitelist.contains(uuid);
	}
	
	/**
	 * @param player
	 * @return
	 */
	public boolean isWhitelisted(Player player) {
		return isWhitelisted(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer
	 * @return
	 */
	public boolean isWhitelisted(OfflinePlayer offlinePlayer) {
		return isWhitelisted(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @return a list of banned players' uuid on the mine.
	 */
	public List<UUID> getBlacklistIDs() {
		return blacklist;
		
	}
	
	/**
	 * @return a list of banned players on the mine.
	 */
	public List<Player> getBlacklist() {
		List<Player> blacklist = new ArrayList();
		for (UUID uuid : this.blacklist) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				blacklist.add(player);
			}
		}
		return blacklist;
	}
	
	/**
	 * @param uuid The uuid of the player to ban.
	 */
	public void blacklist(@Nonnull UUID uuid) {
		if (blacklist.contains(uuid)) {
			return;
		}
		blacklist.add(uuid);
	}
	
	/**
	 * @param player The player to ban.
	 */
	public void blacklist(@Nonnull Player player) {
		blacklist(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer The offline player to ban.
	 */
	public void blacklist(@Nonnull OfflinePlayer offlinePlayer) {
		blacklist(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @param uuid The uuid of the player to unban.
	 */
	public void unblacklist(@Nonnull UUID uuid) {
		if (!blacklist.contains(uuid)) {
			return;
		}
		blacklist.remove(uuid);
	}
	
	/**
	 * @param player The player to unban.
	 */
	public void unblacklist(@Nonnull Player player) {
		unblacklist(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer The offline player to unban.
	 */
	public void unblacklist(@Nonnull OfflinePlayer offlinePlayer) {
		unblacklist(offlinePlayer.getUniqueId());
	}
	
	// TODO documentation
	/**
	 * @param uuid
	 * @return
	 */
	public boolean isBlacklisted(UUID uuid) {
		return blacklist.contains(uuid);
	}

	/**
	 * @param player
	 * @return
	 */
	public boolean isBlacklisted(Player player) {
		return isBlacklisted(player.getUniqueId());
	}

	/**
	 * @param offlinePlayer
	 * @return
	 */
	public boolean isBlacklisted(OfflinePlayer offlinePlayer) {
		return isBlacklisted(offlinePlayer.getUniqueId());
	}
	
	// TODO change

	public List<UUID> getPrioritiesID() {
		return priorities;
	}

	public List<Player> getPriorities() {
		List<Player> priorities = new ArrayList();
		for (UUID uuid : this.priorities) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				priorities.add(player);
			}
		}
		return priorities;
	}
	
	/**
	 * @param uuid The uuid of the player to prioritize.
	 */
	public void prioritize(@Nonnull UUID uuid) {
		if (priorities.contains(uuid)) {
			return;
		}
		priorities.add(uuid);
	}
	
	/**
	 * @param player The player to prioritize.
	 */
	public void prioritize(@Nonnull Player player) {
		prioritize(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer The offline player to prioritize.
	 */
	public void prioritize(@Nonnull OfflinePlayer offlinePlayer) {
		prioritize(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @param uuid The uuid of the player to unprioritize.
	 */
	public void unprioritize(@Nonnull UUID uuid) {
		if (!priorities.contains(uuid)) {
			return;
		}
		priorities.remove(uuid);
	}
	
	/**
	 * @param player The player to unprioritize.
	 */
	public void unprioritize(@Nonnull Player player) {
		unprioritize(player.getUniqueId());
	}
	
	/**
	 * @param offlinePlayer The offline player to unprioritize.
	 */
	public void unprioritize(@Nonnull OfflinePlayer offlinePlayer) {
		unprioritize(offlinePlayer.getUniqueId());
	}
	
	// TODO documentation
	/**
	 * @param uuid
	 * @return
	 */
	public boolean isPrioritized(UUID uuid) {
		return blacklist.contains(uuid);
	}

	/**
	 * @param player
	 * @return
	 */
	public boolean isPrioritized(Player player) {
		return isPrioritized(player.getUniqueId());
	}

	/**
	 * @param offlinePlayer
	 * @return
	 */
	public boolean isPrioritized(OfflinePlayer offlinePlayer) {
		return isPrioritized(offlinePlayer.getUniqueId());
	}
	
	/**
	 * @return the size of the mine.
	 */
	public int getMineSize() {
		return mineSize;
	}
	
	/**
	 * @return the size of the mine.
	 */
	public int getSize() {
		return mineSize;
	}
	
	/**
	 * Opens the mine.
	 */
	public void open() {
		open = true;
	}
	
	/**
	 * Closes the mine.
	 */
	public void close() {
		open = false;
		// TODO Teleport non-whitelisted miners to spawn.
	}
	
	/**
	 * @return true if open, else false.
	 */
	public boolean isOpen() {
		return open;
	}
	
	/**
	 * @return true if closed, else false.
	 */
	public boolean isClosed() {
		return !open;
	}
	
	/**
	 * @return the max amount of miners there can be in the mine.
	 */
	public int getMaxMiners() {
		return maxMiners;
	}
	
	// TODO Documentation
	public boolean isInMine(UUID uuid) {
		return miners.contains(uuid);
	}
	
	public boolean isInMine(Player player) {
		return miners.contains(player.getUniqueId());
	}
	
	/**
	 * @return a list of miners.
	 */
	public List<Player> getMiners() {
		List<Player> miners = new ArrayList<Player>();
		for (UUID uuid : this.miners) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				miners.add(player);
			}
		}
		return miners;
	}
	
	/**
	 * @return a list of the miners' id.
	 */
	public List<UUID> getMinersIds() {
		return miners;
	}
	
	/**
	 * @return the time before the mine can reset.
	 */
	public Long getResetTime() {
		return resetCooldown;
	}
	
	/**
	 * @return the time left before the mine can reset.
	 */
	public Long getResetTimeLeft() {
		return resetCooldown > System.currentTimeMillis() ? System.currentTimeMillis() - resetCooldown : 0;
	}
	
	/**
	 * @return the spawn location of the mine.
	 */
	public Location getSpawn() {
		return spawn;
	}
	
	/**
	 * @return the spawn location of the mine.
	 */
	public Location getSpawnLocation() {
		return spawn;
	}
}
