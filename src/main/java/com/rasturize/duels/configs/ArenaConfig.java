package com.rasturize.duels.configs;

import com.rasturize.duels.Duels;
import com.rasturize.duels.utils.ConfigMan;

public class ArenaConfig extends ConfigMan {

    public ArenaConfig() {
        super("arenas.yml", Duels.getInstance().getDataFolder());
    }
}
