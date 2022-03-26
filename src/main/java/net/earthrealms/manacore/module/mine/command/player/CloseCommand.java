package net.earthrealms.manacore.module.mine.command.player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.EarthCommand;
import net.earthrealms.manacore.lang.Message;
import net.earthrealms.manacore.module.mine.object.Mine;

public class CloseCommand implements EarthCommand {

	@Override
	public List<String> getAliases() {
		return Arrays.asList("open");
	}

	@Override
	public String getPermission() {
		return "Mine.Open";
	}

	@Override
	public String getUsage(Locale locale) {
		return "open";
	}

	@Override
	public String getDescription(Locale locale) {
		return "opens the mine.";
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
		Mine mine = new Mine(island.getUniqueId());
		if (mine.isOpen()) {
			Message.MINE_OPEN_ALREADY.send(commandSender);
			return;
		}
		mine.open();
	}

	@Override
	public List<String> tabComplete(ManaCorePlugin plugin, CommandSender commandSender, String[] args) {
		return null;
	}

}
