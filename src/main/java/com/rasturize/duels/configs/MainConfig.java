package com.rasturize.duels.configs;

import com.rasturize.duels.Duels;
import com.rasturize.duels.utils.ConfigMan;

public class MainConfig extends ConfigMan {

    public MainConfig() {
        super("config.yml", Duels.getInstance().getDataFolder());
    }
}
