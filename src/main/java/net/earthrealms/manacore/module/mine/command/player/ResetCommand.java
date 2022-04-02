package net.earthrealms.manacore.module.mine.command.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.EarthCommand;
import net.earthrealms.manacore.lang.Message;
import net.earthrealms.manacore.module.mine.object.Mine;
import net.earthrealms.manacore.utility.string.StringUtility;

public class ResetCommand implements EarthCommand {

	private Map<UUID, Long> cooldown = new HashMap<UUID, Long>();
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("reset");
	}

	@Override
	public String getPermission() {
		return "Mine.Reset";
	}

	@Override
	public String getUsage(Locale locale) {
		return "reset";
	}

	@Override
	public String getDescription(Locale locale) {
		return "Reset the mine.";
	}

	@Override
	public int getMinArgs() {
		return 0;
	}

	@Override
	public int getMaxArgs() {
		return 0;
	}

	@Override
	public boolean canBeExecutedByConsole() {
		return false;
	}

	@Override
	public boolean displayCommand() {
		return true;
	}

	@Override
	public void execute(ManaCorePlugin plugin, CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		if (SuperiorSkyblockAPI.getIslandAt(((Player) commandSender).getLocation()) == null) {
			Message.MINE_LOCATION_NONE.send(commandSender);
			return;
		}
		Island island = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
		Mine mine = new Mine(player.getUniqueId());
		commandSender.sendMessage(!mine.isOwner(player) + " : " + !mine.isCoOwner(player));
		if (!mine.isOwner(player) && !mine.isCoOwner(player)) {
			Message.SYSTEM_PERMISSION.send(commandSender);
			return;
		}
		
		if (!cooldown.containsKey(island.getUniqueId())) {
			cooldown.put(island.getUniqueId(), System.currentTimeMillis() - 10);
		}
		
		if (cooldown.get(island.getUniqueId()) >= System.currentTimeMillis()) {
			Message.MINE_RESET_COOLDOWN.send(commandSender, StringUtility.timeFormat(System.currentTimeMillis() - cooldown.get(island.getUniqueId())));
			return;
		}
		
		cooldown.put(island.getUniqueId(), System.currentTimeMillis() + (60*1000));
		mine.reset(plugin.getMineHandler().getDefaultRegenDirection());
		Message.MINE_RESET.send(commandSender);
	}

	@Override
	public List<String> tabComplete(ManaCorePlugin plugin, CommandSender sender, String[] args) {
		return null;
	}

}
