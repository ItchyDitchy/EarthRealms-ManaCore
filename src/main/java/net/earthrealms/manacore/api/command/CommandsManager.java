package net.earthrealms.manacore.api.command;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;

public interface CommandsManager {


    /**
     * Register a sub-command.
     *
     * @param earthCommand The sub command to register.
     */
    void registerCommand(EarthCommand earthCommand);

    /**
     * Unregister a sub-command.
     *
     * @param earthCommand The sub command to register.
     */
    void unregisterCommand(EarthCommand earthCommand);

    /**
     * Register a sub-command to the admin command.
     *
     * @param earthCommand The sub command to unregister.
     */
    void registerAdminCommand(EarthCommand earthCommand);

    /**
     * Unregister a sub-command from the admin command.
     *
     * @param earthCommand The sub command to unregister.
     */
    void unregisterAdminCommand(EarthCommand earthCommand);

    /**
     * Get all the registered sub-commands.
     */
    List<EarthCommand> getSubCommands();

    /**
     * Get a sub command by its label.
     *
     * @param commandLabel The label of the sub command.
     * @return The sub command if exists or null.
     */
    @Nullable
    EarthCommand getCommand(String commandLabel);

    /**
     * Get all the registered admin sub-commands.
     */
    List<EarthCommand> getAdminSubCommands();

    /**
     * Get an admin sub command by its label.
     *
     * @param commandLabel The label of the sub command.
     * @return The sub command if exists or null.
     */
    @Nullable
    EarthCommand getAdminCommand(String commandLabel);

    /**
     * Dispatch a sub command.
     *
     * @param sender     The sender to dispatch the command.
     * @param subCommand The sub-command to dispatch.
     */
    void dispatchSubCommand(CommandSender sender, String subCommand);

    /**
     * Dispatch a sub command.
     *
     * @param sender     The sender to dispatch the command.
     * @param subCommand The sub-command to dispatch.
     * @param args       List of arguments of the sub-command.
     */
    void dispatchSubCommand(CommandSender sender, String subCommand, String args);

}