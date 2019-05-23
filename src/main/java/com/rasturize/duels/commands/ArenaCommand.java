package com.rasturize.duels.commands;

import com.rasturize.duels.Duels;
import com.rasturize.duels.type.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only command");
            return true;
        }

        Player player = (Player)sender;
        if (!player.hasPermission("duels.admin")) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY + "/arena menu");
            player.sendMessage(ChatColor.GRAY + "/arena setkit");
            player.sendMessage(ChatColor.GRAY + "/arena create <name>");
            player.sendMessage(ChatColor.GRAY + "/arena addarena <name>");
            player.sendMessage(ChatColor.GRAY + "/arena setpos1 <name> <id>");
            player.sendMessage(ChatColor.GRAY + "/arena setpos2 <name> <id>");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("menu")) {
                player.openInventory(Duels.getInstance().getArenaManager().getArenaInventory());
            } else if (args[0].equalsIgnoreCase("setkit")) {
                Duels.getInstance().getArenaManager().setKit(player);
            }
        }  else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                Duels.getInstance().getArenaManager().createArena(player, args[1]);
            } else if (args[0].equalsIgnoreCase("addarena")) {
                Duels.getInstance().getArenaManager().addUsableArena(player, args[1]);
            }
        } else if (args.length == 3) {
            Arena arena = Duels.getInstance().getArenaManager().getArena(args[1]);
            if (arena == null) {
                player.sendMessage(ChatColor.RED + args[1] + " not found!");
                return true;
            }

            int id = 0;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                player.sendMessage(ChatColor.RED + "" + id + " is not a valid sub arena!");
                return true;
            }
            if (id > arena.getTotal() || id < 0) {
                player.sendMessage(ChatColor.RED + "" + id + " is not a valid sub arena!");
                return true;
            }
            Duels.getInstance().getArenaManager().setSpawnPoint(player, arena, id, args[0].equalsIgnoreCase("setpos1") ? 1 : 2);
        }
        return false;
    }
}
