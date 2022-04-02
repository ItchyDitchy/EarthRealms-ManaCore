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

public class MinersCommand implements EarthCommand {

	@Override
	public List<String> getAliases() {
		return Arrays.asList("miners");
	}

	@Override
	public String getPermission() {
		return "Mine.Miners";
	}

	@Override
	public String getUsage(Locale locale) {
		return "miners";
	}

	@Override
	public String getDescription(Locale locale) {
		return "Lists the miners of the mine.";
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
	public void execute(ManaCorePlugin plugin, CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Island island = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
		if (island == null) {
			Message.INVALID_MINE.send(sender, "Your location");
			return;
		}
		
		String minersList = "";
		List<SuperiorPlayer> visitors = island.getIslandVisitors();
		if (visitors.size() == 0) {
			Message.MINE_MINERS_NONE.send(sender);
			return;
		}
		for (int index = 0; index < visitors.size(); index++) {
			SuperiorPlayer visitor = visitors.get(index);
			if (index == 0) {
				minersList = visitor.getName();
				continue;
			}
			if (index == visitors.size()) {
				minersList.concat((visitors.size() == 2 ? "" : ",") + " and" + visitor.getName());
				break;
			}
			minersList.concat("" + visitor.getName());
		}
		
		// TODO Change third parameter to max miners.
		Message.MINE_MINERS.send(sender, visitors.size(), visitors.size(), minersList);
	}

	@Override
	public List<String> tabComplete(ManaCorePlugin plugin, CommandSender sender, String[] args) {
		return null;
	}

}
