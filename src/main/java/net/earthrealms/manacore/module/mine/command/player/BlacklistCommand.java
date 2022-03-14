package net.earthrealms.manacore.module.mine.command.player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.EarthCommand;

public class BlacklistCommand implements EarthCommand {

	@Override
	public List<String> getAliases() {
		return Arrays.asList("blacklist");
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinArgs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxArgs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canBeExecutedByConsole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean displayCommand() {
		return true;
	}

	@Override
	public void execute(ManaCorePlugin plugin, CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> tabComplete(ManaCorePlugin plugin, CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
