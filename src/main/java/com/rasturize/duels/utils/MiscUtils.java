package com.rasturize.duels.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MiscUtils {

    public static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
    }

    public static Location getSerializedLocation(String serializedLocation) {
        if (serializedLocation != null) {
            final String[] parts = serializedLocation.split(":");
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = 0;
            float pitch = 0;
            try {
                yaw = Float.parseFloat(parts[4]);
                pitch = Float.parseFloat(parts[5]);
            } catch (Exception ex) {}
            return new Location(Bukkit.getServer().getWorld(parts[0]), x, y, z, yaw, pitch);
        }
        return null;
    }

    public static ItemStack createItem(Material material, int amount, String display, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', display));
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    public static int freeSlot(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                return i;
            }
        }
        return 0;
    }

    public static boolean inventoryEmpty(Inventory inventory) {
        for (int i = 0; i < inventory.getSize()+4; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }
}
