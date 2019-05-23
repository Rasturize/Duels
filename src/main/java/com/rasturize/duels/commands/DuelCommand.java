package com.rasturize.duels.commands;

import com.rasturize.duels.Duels;
import com.rasturize.duels.type.Request;
import com.rasturize.duels.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only command");
            return true;
        }

        Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY + "/duel <player>");
            player.sendMessage(ChatColor.GRAY + "/duel accept <player>");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + args[0] + " is not online!");
                return true;
            }
            Duels.getInstance().getArenaManager().createRequest(player, target);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (Duels.getInstance().getArenaManager().getRequests().containsKey(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You cannot accept a duel request when you have a pending one sent out already!");
                    return true;
                }
                if (!MiscUtils.inventoryEmpty(player.getInventory())) {
                    player.sendMessage(ChatColor.RED + "Your inventory must be empty to accept a duel request!");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + args[0] + " is not online!");
                    return true;
                }

                Request request = Duels.getInstance().getArenaManager().getRequests().get(target.getUniqueId());
                if (request == null) {
                    player.sendMessage(ChatColor.RED + "No request from " + args[1] + " could be found!");
                    return true;
                }
                if (!request.isSent()) {
                    player.sendMessage(ChatColor.RED + "Wait for " + args[1] + " to pick an arena before accepting his request!");
                    return true;
                }
                request.acceptDuel();
            }
        }
        return true;
    }
}
