package com.rasturize.duels;

import com.rasturize.duels.commands.DuelCommand;
import com.rasturize.duels.commands.ArenaCommand;
import com.rasturize.duels.configs.ArenaConfig;
import com.rasturize.duels.configs.MainConfig;
import com.rasturize.duels.listeners.ArenaListener;
import com.rasturize.duels.listeners.MenuListener;
import com.rasturize.duels.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Original plugin created by Tyler (http://tylerog.tech/)
 * Now maintained by Rasturize
 */

public class Duels extends JavaPlugin {

    private static Duels instance;
    private MainConfig mainConfig;
    private ArenaConfig arenaConfig;
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new MainConfig();
        this.arenaConfig = new ArenaConfig();
        this.arenaManager = new ArenaManager();

        getCommand("arena").setExecutor(new ArenaCommand());
        getCommand("duel").setExecutor(new DuelCommand());

        Bukkit.getPluginManager().registerEvents(new ArenaListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
    }

    public static Duels getInstance() {
        return instance;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}
