package com.rasturize.duels.type;


import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class Arena {

    private String arenaName, displayName;
    private Map<Integer, SpawnPoints> ids;

    private int total = 0;

    public Arena(String arenaName, String displayName) {
        this.arenaName = arenaName;
        this.displayName = displayName;
        this.ids = new HashMap<>();
    }

    /**
     * Set a spawn point position.
     *
     * @param id Unique sub identifier.
     * @param pos Position number, 1 or 2.
     * @param location
     */
    public void setPosition(int id, int pos, Location location) {
        ids.put(id, pos == 1 ? ids.get(id).setPos1(location) : ids.get(id).setPos2(location));
    }

    /**
     * Arena name used for identification.
     *
     * @return
     */
    public String getArenaName() {
        return arenaName;
    }

    /**
     * Arena name used for display purposes.
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set total arenas, update
     * when creating another arena.
     *
     * @param total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * If 0, then there are
     * no arenas.
     *
     * @return
     */
    public int getTotal() {
        return total;
    }

    public Map<Integer, SpawnPoints> getIds() {
        return ids;
    }

    public SpawnPoints getSpawnPoints(int id) {
        return ids.get(id);
    }

    public class SpawnPoints {

        private Location pos1, pos2;

        public SpawnPoints setPos1(Location pos1) {
            this.pos1 = pos1;
            return this;
        }

        public SpawnPoints setPos2(Location pos2) {
            this.pos2 = pos2;
            return this;
        }

        public Location getPos1() {
            return pos1;
        }

        public Location getPos2() {
            return pos2;
        }
    }
}
