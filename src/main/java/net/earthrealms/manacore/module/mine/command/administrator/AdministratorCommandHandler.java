package net.earthrealms.manacore.module.mine.command.administrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.EarthCommand;
import net.earthrealms.manacore.lang.Message;
import net.earthrealms.manacore.module.mine.command.player.ResetCommand;

public class AdministratorCommandHandler  {

	private List<EarthCommand> commands = new ArrayList<EarthCommand>();
	
	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	public AdministratorCommandHandler() {
		commands.add(new ResetCommand());
	}
	
	public boolean execute(CommandSender commandSender, String[] args) {
		for (EarthCommand earthCommand : commands) {
			if (!earthCommand.getAliases().contains(args[0].toLowerCase())) {
				continue;
			}
			if (!commandSender.hasPermission(earthCommand.getPermission())) {
				Message.SYSTEM_PERMISSION.send(commandSender, earthCommand.getPermission());
				return true;
			}
			if (!earthCommand.canBeExecutedByConsole()) {
				if (commandSender instanceof ConsoleCommandSender) {
					Message.SYSTEM_ONLY_PLAYER.send(commandSender);
					return true;
				}
			}
			if (args.length - 1 < earthCommand.getMinArgs()) {
				Message.USAGE.send(commandSender, earthCommand.getUsage(Locale.ENGLISH));
				return true;
			}
			if (args.length - 1 > earthCommand.getMaxArgs()) {
				Message.USAGE.send(commandSender, earthCommand.getUsage(Locale.ENGLISH));
				return true;
			}
			earthCommand.execute(plugin, commandSender, args);
			return true;
		}
		return false;
	}
}
