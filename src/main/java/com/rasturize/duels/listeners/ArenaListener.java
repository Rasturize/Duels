package com.rasturize.duels.listeners;

import com.rasturize.duels.Duels;
import com.rasturize.duels.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaListener implements Listener {

    private ArenaManager arenaManager = Duels.getInstance().getArenaManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaDeath(PlayerDeathEvent event) {
        if (arenaManager.getDueling().containsKey(event.getEntity().getUniqueId())) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            arenaManager.endDuel(event.getEntity());
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (arenaManager.getDueling().containsKey(event.getPlayer().getUniqueId())) {
            arenaManager.endDuel(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        arenaManager.getRequests().remove(event.getPlayer().getUniqueId());
        if (arenaManager.getDueling().containsKey(event.getPlayer().getUniqueId())) {
            arenaManager.endDuel(event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (arenaManager.getDueling().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (arenaManager.getDueling().containsKey(event.getDamager().getUniqueId())) {
                if (event.isCancelled()) {
                    event.setCancelled(false);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (arenaManager.getDueling().containsKey(event.getPlayer().getUniqueId())) {
            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                event.getItemDrop().remove();
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (arenaManager.getDueling().containsKey(event.getPlayer().getUniqueId())) {
            if (event.getMessage().startsWith("/msg") || event.getMessage().startsWith("/message") || event.getMessage().startsWith("/r") || event.getMessage().startsWith("/reply")) {
                return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot type commands while in a duel!");
        }
    }
}
