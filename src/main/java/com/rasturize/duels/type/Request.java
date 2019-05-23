package com.rasturize.duels.type;

import com.rasturize.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Request {

    private final UUID requester, target;
    private Arena arena;
    private int arenaID;
    private boolean accepted, sent = false;

    public Request(UUID requester, UUID target) {
        this.requester = requester;
        this.target = target;
    }

    public Arena getArena() {
        return arena;
    }

    public void acceptDuel() {
        this.accepted = true;
        Duels.getInstance().getArenaManager().startDuel(Bukkit.getPlayer(requester), Bukkit.getPlayer(target), arena.getSpawnPoints(arenaID));
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isSent() {
        return sent;
    }

    public void sendRequest(Arena arena) {
        this.arena = arena;
        this.arenaID = arena.getTotal();
        this.arena.setTotal(arena.getTotal()-1);

        Player requestPlayer = Bukkit.getPlayer(requester);
        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer == null) {
            requestPlayer.sendMessage(ChatColor.RED + targetPlayer.getName() + " is no longer online!");
            return;
        }

        if (arena == null) {
            requestPlayer.sendMessage(ChatColor.RED + "Invalid arena ' " + arena.getArenaName() + "' error, please report to developer.");
            return;
        }
        sent = true;

        requestPlayer.sendMessage(ChatColor.GREEN + "You've sent a duel request to " + targetPlayer.getName() + "!");
        targetPlayer.sendMessage(requestPlayer.getDisplayName() + ChatColor.GOLD + " has requested to duel. " + ChatColor.AQUA + "[" + arena.getArenaName() + "]");
        targetPlayer.sendMessage(ChatColor.GOLD + "To accept the duel, type: " + ChatColor.GREEN + "/duel accept " + requestPlayer.getName() + "");

        Bukkit.getScheduler().runTaskLater(Duels.getInstance(), ()-> {
            if (!isAccepted()) {
                Duels.getInstance().getArenaManager().getRequests().remove(requester);
                requestPlayer.sendMessage(ChatColor.RED + "Your duel request has expired!");
                this.arena.setTotal(arena.getTotal()+1);
            }
        }, 20*20);
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }
}
