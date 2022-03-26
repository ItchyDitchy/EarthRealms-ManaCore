package net.earthrealms.manacore.module.mine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.function.pattern.RandomPattern;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.lang.Message;
import net.earthrealms.manacore.module.mine.command.administrator.AdministratorCommandHandler;
import net.earthrealms.manacore.module.mine.command.developer.DeveloperCommandHandler;
import net.earthrealms.manacore.module.mine.command.player.PlayerCommandHandler;

public class MineCommand implements CommandExecutor, TabCompleter {

	private int size = 9;
	private WorldEditPlugin worldEdit = WorldEditPlugin.getInstance();
	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();

	private PlayerCommandHandler playerCommandHandler = new PlayerCommandHandler();
	private AdministratorCommandHandler administratorCommandHandler = new AdministratorCommandHandler();
	private DeveloperCommandHandler developerCommandHandler = new DeveloperCommandHandler();

	private Map<UUID, RandomPattern> mineBlocks = new HashMap<UUID, RandomPattern>();

	public MineCommand() {
		plugin.getCommand("tmine").setExecutor(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("Mine.Default")) {
			Message.SYSTEM_PERMISSION.send(sender);
			return true;
		}

		if (args.length == 0) {
			if (sender instanceof Player) {
				SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer((Player) sender);
				if (!superiorPlayer.hasIsland()) {
					plugin.getMineHandler().createMine(superiorPlayer.getUniqueId());
				}
				Island island = superiorPlayer.getIsland();
				superiorPlayer.teleport(island);
			}
			return true;
		}

		if (playerCommandHandler.execute(sender, args)) {
			return true;
		}

		if (!args[0].equalsIgnoreCase("Administrator")) {
			if (!sender.hasPermission("Mine.Administrator")) {
				Message.SYSTEM_PERMISSION.send(sender);
				return true;
			}

			List<String> newList = new ArrayList<String>(Arrays.asList(args));
			newList.remove(0);
			if (administratorCommandHandler.execute(sender, newList.toArray(new String[0]))) {
				return true;
			}

			if (!sender.hasPermission("Mine.Help")) {
				Message.SYSTEM_PERMISSION.send(sender);
				return true;
			}
			Message.MINE_HELP.send(sender);
			return true;
		}

		if (!args[0].equalsIgnoreCase("Developer")) {
			if (!sender.hasPermission("Mine.Developer")) {
				Message.SYSTEM_PERMISSION.send(sender);
				return true;
			}

			List<String> newList = new ArrayList<String>(Arrays.asList(args));
			newList.remove(0);
			if (developerCommandHandler.execute(sender, newList.toArray(new String[0]))) {
				return true;
			}

			if (!sender.hasPermission("Mine.Help")) {
				Message.SYSTEM_PERMISSION.send(sender);
				return true;
			}
			Message.MINE_HELP.send(sender);
			return true;
		}

		if (!sender.hasPermission("Mine.Help")) {
			Message.SYSTEM_PERMISSION.send(sender);
			return true;
		}

		Message.MINE_HELP.send(sender);
		return true;
	}

}
