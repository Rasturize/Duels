package com.rasturize.duels.listeners;

import com.rasturize.duels.Duels;
import com.rasturize.duels.type.Arena;
import com.rasturize.duels.managers.ArenaManager;
import com.rasturize.duels.type.Request;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private ArenaManager arenaManager = Duels.getInstance().getArenaManager();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().getName().equals(Duels.getInstance().getArenaManager().getArenaInventory().getName())) {
                Player requester = (Player)event.getWhoClicked();
                event.setCancelled(true);

                Request request = arenaManager.getRequests().get(requester.getUniqueId());
                if (request == null) {
                    return;
                }

                final ItemStack current = event.getCurrentItem();
                if (current != null && current.getType() != Material.AIR) {
                    for (Arena arena : arenaManager.getArenas()) {
                        if (arena.getDisplayName().contains(ChatColor.stripColor(current.getItemMeta().getDisplayName()))) {
                            if (arena.getTotal() < 1) {
                                arenaManager.getRequests().remove(requester.getUniqueId());
                                requester.closeInventory();
                                requester.sendMessage(ChatColor.RED + "No available arenas!");
                                return;
                            }

                            request.sendRequest(arena);
                            requester.closeInventory();
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Request request = arenaManager.getRequests().get(event.getPlayer().getUniqueId());
        if (request == null) {
            return;
        }

        if (!request.isSent()) {
            arenaManager.getRequests().remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(ChatColor.RED + "Duel request canceled!");
        }
    }
}
