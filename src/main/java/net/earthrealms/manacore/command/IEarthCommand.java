package net.earthrealms.manacore.command;

import net.earthrealms.manacore.api.command.EarthCommand;

public interface IEarthCommand extends EarthCommand {

    default boolean displayCommand() {
        return true;
    }

}
