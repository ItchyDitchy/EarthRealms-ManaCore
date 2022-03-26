package net.earthrealms.manacore.module.mine.command.player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.EarthCommand;

public class TeleportCommand implements EarthCommand {

	@Override
	public List<String> getAliases() {
		return Arrays.asList("teleport","tp");
	}

	@Override
	public String getPermission() {
		return "Mine.Teleport";
	}

	@Override
	public String getUsage(Locale locale) {
		return "teleport";
	}

	@Override
	public String getDescription(Locale locale) {
		return "Teleport to your mine!";
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
		SuperiorPlayer player = SuperiorSkyblockAPI.getPlayer((Player) sender);
		player.teleport(player.getIsland().getIslandHome(Environment.NORMAL));
	}

	@Override
	public List<String> tabComplete(ManaCorePlugin plugin, CommandSender sender, String[] args) {
		return null;
	}

}
