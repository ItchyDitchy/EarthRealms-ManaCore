package net.earthrealms.manacore.module.mine;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.mine.command.MineCommand;

public class MineHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	public MineHandler() {
		new MineCommand();
	}
	
	public void resetMine(SuperiorPlayer superiorPlayer) {
		
	}
}
