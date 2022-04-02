package net.earthrealms.manacore.module.mine.command.administrator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.EarthCommand;
import net.earthrealms.manacore.lang.Message;
import net.earthrealms.manacore.module.mine.object.Mine;

public class WhitelistCommand implements EarthCommand {

	@Override
	public List<String> getAliases() {
		return Arrays.asList("whitelist");
	}

	@Override
	public String getPermission() {
		return "Mine.Whitelist";
	}

	@Override
	public String getUsage(Locale locale) {
		return "whitelist "
				+ "<remove/add/list> "
				+ "<" + Message.VALUE_PLAYER.getMessage(Locale.ENGLISH) + ">";
	}

	@Override
	public String getDescription(Locale locale) {
		return "Manage whitelist.";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 2;
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
		SuperiorPlayer target = SuperiorSkyblockAPI.getPlayer(args[2]);
		if (target == null) {
			Message.INVALID_PLAYER.send(commandSender, args[2]);
			return;
		}
		Island island = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
		Mine mine = new Mine(island.getUniqueId());
		if (!mine.isOwner(player) && !mine.isCoOwner(player)) {
			Message.SYSTEM_PERMISSION.send(commandSender);
			return;
		}
		if (args[1].equalsIgnoreCase("remove")) {
			if (!mine.isWhitelisted(player)) {
				Message.MINE_WHITELIST_REMOVE_ALREADY.send(commandSender, args[2]);
				return;
			}
			mine.unwhitelist(player);
			Message.MINE_WHITELIST_REMOVE.send(commandSender, target.getName());
			return;
		}
		if (args[1].equalsIgnoreCase("add")) {
			if (mine.isWhitelisted(player)) {
				Message.MINE_WHITELIST_ADD_ALREADY.send(commandSender, args[2]);
				return;
			}
			mine.whitelist(player);
			Message.MINE_WHITELIST_ADD.send(commandSender, target.getName());
			return;
		}
		if (args[1].equalsIgnoreCase("list")) {
			if (mine.getWhitelist().isEmpty()) {
				Message.MINE_WHITELIST_LIST_NONE.send(commandSender);
				return;
			}
			String whitelist = "";
			Message.MINE_WHITELIST_LIST.send(commandSender, whitelist);
			return;
		}
		Message.USAGE.send(commandSender, getUsage(Locale.ENGLISH));
	}

	@Override
	public List<String> tabComplete(ManaCorePlugin plugin, CommandSender commandSender, String[] args) {
		return null;
	}

}