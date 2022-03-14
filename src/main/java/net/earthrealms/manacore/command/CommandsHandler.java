package net.earthrealms.manacore.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.command.CommandsManager;
import net.earthrealms.manacore.api.command.CommandsMap;
import net.earthrealms.manacore.api.command.EarthCommand;
import net.earthrealms.manacore.handler.AbstractHandler;
import net.earthrealms.manacore.lang.Message;
import net.earthrealms.manacore.lang.PlayerLocales;
import net.earthrealms.manacore.utility.debug.PluginDebugger;

public class CommandsHandler extends AbstractHandler implements CommandsManager {

    private final Map<UUID, Map<String, Long>> commandsCooldown = new HashMap<>();

    private final CommandsMap playerCommandsMap;
    private final CommandsMap adminCommandsMap;
    private final CommandsMap developerCommandsMap;

    private Set<Runnable> pendingCommands = new HashSet<>();

    private PluginCommand pluginCommand;
    private String label = null;

    public CommandsHandler(ManaCorePlugin plugin, CommandsMap playerCommandsMap, CommandsMap adminCommandsMap, CommandsMap developerCommandsMap) {
        super(plugin);
        this.playerCommandsMap = playerCommandsMap;
        this.adminCommandsMap = adminCommandsMap;
        this.developerCommandsMap = developerCommandsMap;
    }

    @Override
    public void loadData() {
        String islandCommand = "mine";//plugin.getSettingsHandler().getIslandCommand();
        label = islandCommand.split(",")[0];

        pluginCommand = new PluginCommand(label);

        String[] commandSections = islandCommand.split(",");

        if (commandSections.length > 1) {
            pluginCommand.setAliases(Arrays.asList(Arrays.copyOfRange(commandSections, 1, commandSections.length)));
        }

        //plugin.getNMSAlgorithms().registerCommand(pluginCommand);

        playerCommandsMap.loadDefaultCommands();
        adminCommandsMap.loadDefaultCommands();

        if (this.pendingCommands != null) {
            Set<Runnable> pendingCommands = new HashSet<>(this.pendingCommands);
            this.pendingCommands = null;
            pendingCommands.forEach(Runnable::run);
        }
    }

    @Override
    public void registerCommand(EarthCommand superiorCommand) {
        Preconditions.checkNotNull(superiorCommand, "superiorCommand parameter cannot be null.");
        registerCommand(superiorCommand, true);
    }

    @Override
    public void unregisterCommand(EarthCommand superiorCommand) {
        playerCommandsMap.unregisterCommand(superiorCommand);
    }

    @Override
    public void registerAdminCommand(EarthCommand superiorCommand) {
        if (pendingCommands != null) {
            pendingCommands.add(() -> registerAdminCommand(superiorCommand));
            return;
        }

        Preconditions.checkNotNull(superiorCommand, "superiorCommand parameter cannot be null.");
        adminCommandsMap.registerCommand(superiorCommand, true);
    }

    @Override
    public void unregisterAdminCommand(EarthCommand superiorCommand) {
        Preconditions.checkNotNull(superiorCommand, "superiorCommand parameter cannot be null.");
        adminCommandsMap.unregisterCommand(superiorCommand);
    }

    @Override
    public List<EarthCommand> getSubCommands() {
        return playerCommandsMap.getSubCommands();
    }

    @Nullable
    @Override
    public EarthCommand getCommand(String commandLabel) {
        return playerCommandsMap.getCommand(commandLabel);
    }

    @Override
    public List<EarthCommand> getAdminSubCommands() {
        return adminCommandsMap.getSubCommands();
    }

    @Nullable
    @Override
    public EarthCommand getAdminCommand(String commandLabel) {
        return adminCommandsMap.getCommand(commandLabel);
    }

    @Override
    public void dispatchSubCommand(CommandSender sender, String subCommand) {
        dispatchSubCommand(sender, subCommand, "");
    }

    @Override
    public void dispatchSubCommand(CommandSender sender, String subCommand, String args) {
        String[] argsSplit = args.split(" ");
        String[] commandArguments = new String[argsSplit.length + 1];
        commandArguments[0] = subCommand;
        System.arraycopy(argsSplit, 0, commandArguments, 1, argsSplit.length);
        pluginCommand.execute(sender, "", commandArguments);
    }

    public String getLabel() {
        return label;
    }

    public void registerCommand(EarthCommand superiorCommand, boolean sort) {
        if (pendingCommands != null) {
            pendingCommands.add(() -> registerCommand(superiorCommand, sort));
            return;
        }

        playerCommandsMap.registerCommand(superiorCommand, sort);
    }

    private EarthCommand createInstance(Class<?> clazz) throws Exception {
        Preconditions.checkArgument(EarthCommand.class.isAssignableFrom(clazz), "Class " + clazz + " is not a EarthCommand.");

        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                if (!constructor.isAccessible())
                    constructor.setAccessible(true);

                return (EarthCommand) constructor.newInstance();
            }
        }

        throw new IllegalArgumentException("Class " + clazz + " has no valid constructors.");
    }

    private class PluginCommand extends BukkitCommand {

        PluginCommand(String islandCommandLabel) {
            super(islandCommandLabel);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            java.util.Locale locale = PlayerLocales.getLocale(sender);

            if (args.length > 0) {
                EarthCommand command = playerCommandsMap.getCommand(args[0]);
                if (command != null) {
                    if (!(sender instanceof Player) && !command.canBeExecutedByConsole()) {
                        Message.CUSTOM.send(sender, "&cCan be executed only by players!", true);
                        return false;
                    }

                    if (!command.getPermission().isEmpty() && !sender.hasPermission(command.getPermission())) {
                        PluginDebugger.debug("Action: Execute Command, Player: " + sender.getName() + ", Command: " + args[0] + ", Missing Permission: " + command.getPermission());
                        Message.SYSTEM_PERMISSION.send(sender, locale);
                        return false;
                    }

                    if (args.length < command.getMinArgs() || args.length > command.getMaxArgs()) {
                        Message.USAGE.send(sender, locale, getLabel() + " " + command.getUsage(locale));
                        return false;
                    }

                    command.execute(plugin, sender, args);
                    return false;
                }
            }

            Message.SYSTEM_PERMISSION.send(sender);

            return false;
        }


        @Override
        public List<String> tabComplete(CommandSender sender, String label, String[] args) {
            if (args.length > 0) {
                EarthCommand command = playerCommandsMap.getCommand(args[0]);
                if (command != null) {
                    return command.getPermission() != null && !sender.hasPermission(command.getPermission()) ?
                            new ArrayList<>() : command.tabComplete(plugin, sender, args);
                }
            }

            List<String> list = new ArrayList<>();

            return list;
        }

    }

}