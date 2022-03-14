package net.earthrealms.manacore.api.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.earthrealms.manacore.ManaCorePlugin;

public abstract class CommandsMap {

    private final Map<String, EarthCommand> subCommands = new LinkedHashMap<>();
    private final Map<String, EarthCommand> aliasesToCommand = new HashMap<>();

    private final ManaCorePlugin plugin;

    protected CommandsMap(ManaCorePlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void loadDefaultCommands();

    public void registerCommand(EarthCommand earthCommand, boolean sort) {
        List<String> aliases = new ArrayList<>(earthCommand.getAliases());
        String label = aliases.get(0).toLowerCase();

        if (subCommands.containsKey(label)) {
            subCommands.remove(label);
            aliasesToCommand.values().removeIf(sC -> sC.getAliases().get(0).equals(aliases.get(0)));
        }
        subCommands.put(label, earthCommand);

        for (int i = 1; i < aliases.size(); i++) {
            aliasesToCommand.put(aliases.get(i).toLowerCase(), earthCommand);
        }

        if (sort) {
            List<EarthCommand> superiorCommands = new ArrayList<>(subCommands.values());
            superiorCommands.sort(Comparator.comparing(o -> o.getAliases().get(0)));
            subCommands.clear();
            superiorCommands.forEach(s -> subCommands.put(s.getAliases().get(0), s));
        }
    }

    public void unregisterCommand(EarthCommand earthCommand) {
        Preconditions.checkNotNull(earthCommand, "earthCommand parameter cannot be null.");

        List<String> aliases = new ArrayList<>(earthCommand.getAliases());
        String label = aliases.get(0).toLowerCase();

        subCommands.remove(label);
        aliasesToCommand.values().removeIf(sC -> sC.getAliases().get(0).equals(aliases.get(0)));
    }

    @Nullable
    public EarthCommand getCommand(String label) {
        label = label.toLowerCase();
        return subCommands.getOrDefault(label, aliasesToCommand.get(label));
    }

    public List<EarthCommand> getSubCommands() {
        return Collections.unmodifiableList(new ArrayList<>(subCommands.values()));
    }

}