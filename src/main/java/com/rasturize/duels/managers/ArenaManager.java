package com.rasturize.duels.managers;

import com.rasturize.duels.Duels;
import com.rasturize.duels.type.Arena;
import com.rasturize.duels.utils.MiscUtils;
import com.rasturize.duels.configs.ArenaConfig;
import com.rasturize.duels.configs.MainConfig;
import com.rasturize.duels.type.Request;
import com.rasturize.duels.utils.Menu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager {

    private MainConfig mainConfig = Duels.getInstance().getMainConfig();
    private ArenaConfig arenaConfig = Duels.getInstance().getArenaConfig();

    private Map<UUID, Request> requests = new HashMap<>();
    private Map<UUID, UUID> dueling = new HashMap<>();
    private List<Arena> arenas = new ArrayList<>();

    private Menu arenaMenu;
    private ItemStack[] kitItems, armorItems;

    public ArenaManager() {
        this.arenaMenu = new Menu(null, "Choose an arena...", 9);
        this.load();
    }

    public void createRequest(Player player, Player target) {
        if (!MiscUtils.inventoryEmpty(player.getInventory())) {
            player.sendMessage(ChatColor.RED + "Your inventory must be empty to send a duel request!");
            return;
        }
        if (player.getName().equals(target.getName())) {
            player.sendMessage(ChatColor.RED + "You cannot duel yourself!");
            return;
        }
        if (requests.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have an active duel request already, please wait till it expires.");
            return;
        }
        if (requests.containsKey(target.getUniqueId())) {
            if (requests.get(target.getUniqueId()).getTarget().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + target.getName() + " has sent you a duel request, accept his.");
                return;
            }
        }

        requests.put(player.getUniqueId(), new Request(player.getUniqueId(), target.getUniqueId()));
        player.openInventory(getArenaInventory());
    }

    public void startDuel(Player player1, Player player2, Arena.SpawnPoints spawnPoints) {
        dueling.put(player1.getUniqueId(), player2.getUniqueId());
        dueling.put(player2.getUniqueId(), player1.getUniqueId());

        player1.teleport(spawnPoints.getPos1());
        player1.playSound(player1.getLocation(), Sound.NOTE_PLING, 2, 2);
        player1.getInventory().setContents(kitItems);
        player1.getInventory().setArmorContents(armorItems);
        player1.setHealth(20);
        player1.setFoodLevel(20);

        player2.teleport(spawnPoints.getPos2());
        player2.playSound(player2.getLocation(), Sound.NOTE_PLING, 2, 2);
        player2.getInventory().setContents(kitItems);
        player2.getInventory().setArmorContents(armorItems);
        player2.setHealth(20);
        player2.setFoodLevel(20);
    }

    public void endDuel(Player loser) {
        Player winner = Bukkit.getPlayer(dueling.get(loser.getUniqueId()));
        BigDecimal hp = new BigDecimal(winner.getHealth()/2.0).setScale(2, RoundingMode.HALF_UP);

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getName().equals(winner.getName()) || player.getName().equals(loser.getName()))
                .collect(Collectors.toList()).forEach(player -> {

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "Winner: " + ChatColor.GREEN + winner.getName() + " [" + hp.doubleValue() + "]");
            player.sendMessage(ChatColor.GOLD + "Loser: " + ChatColor.RED + loser.getName() + " [Dead]");
            player.sendMessage("");

            player.setHealth(20);
            player.setFoodLevel(20);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

            dueling.remove(player.getUniqueId());

            Bukkit.getScheduler().runTaskLater(Duels.getInstance(), ()-> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
                player.setFireTicks(0);
            }, 5);
            player.teleport(Bukkit.getWorld("world").getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        });

        Request request = requests.get(winner.getUniqueId());
        if (request == null) {
            request = requests.get(loser.getUniqueId());
        }

        request.getArena().setTotal(request.getArena().getTotal()+1);
        requests.remove(loser.getUniqueId());
        requests.remove(winner.getUniqueId());
    }

    public Map<UUID, Request> getRequests() {
        return requests;
    }

    public Map<UUID, UUID> getDueling() {
        return dueling;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void load() {
        for (String arenaName : arenaConfig.getSection("", false)) {
            Arena arena = new Arena(arenaName, arenaConfig.getConfig().getString(arenaName + ".display-name"));
            if (!arenaConfig.getConfig().contains(arenaName + ".ids")) {
                continue;
            }

            int am = 0;
            for (String id : arenaConfig.getSection(arenaName + ".ids", false)) {
                if (!arenaConfig.getConfig().contains(arenaName + ".ids." + id)) {
                    continue;
                }

                Arena.SpawnPoints spawnPoints = arena.new SpawnPoints();
                for (String pos : arenaConfig.getSection(arenaName + ".ids." + id, false)) {
                    if (pos.equals("pos1")) spawnPoints.setPos1(MiscUtils.getSerializedLocation(arenaConfig.getConfig().getString(arenaName + ".ids." + id + "." + pos)));

                    if (pos.equals("pos2")) spawnPoints.setPos2(MiscUtils.getSerializedLocation(arenaConfig.getConfig().getString(arenaName + ".ids." + id + "." + pos)));
                }
                arena.getIds().put(Integer.parseInt(id), spawnPoints);
                am++;
            }
            arena.setTotal(am);
            arenas.add(arena);
        }

        if (mainConfig.getConfig().contains("Kit")) {
            ArrayList is = (ArrayList)mainConfig.getConfig().get("Kit.items");
            this.kitItems = (ItemStack[])is.toArray(new ItemStack[is.size()]);
            is = (ArrayList)mainConfig.getConfig().get("Kit.armor");
            this.armorItems = (ItemStack[])is.toArray(new ItemStack[is.size()]);
        }

        arenaMenu.clearInventory();
        Bukkit.getScheduler().runTaskTimer(Duels.getInstance(), ()-> arenas.forEach(arena -> arenaMenu.setItem(arenas.indexOf(arena), MiscUtils.createItem(Material.PAPER, 1, arena.getDisplayName(), "", ChatColor.GRAY + "Available: " + ChatColor.GREEN + arena.getTotal()))), 20, 20*10);
    }

    public Inventory getArenaInventory() {
        return arenaMenu.getInventory();
    }

    public Arena getArena(String arenaName) {
        return arenas.stream().filter(arena -> arena.getArenaName().equalsIgnoreCase(arenaName)).findFirst().get();
    }

    public void createArena(Player player, String arenaName) {
        if (arenaConfig.getConfig().contains(arenaName)) {
            player.sendMessage(ChatColor.RED + arenaName + " already exist!");
            return;
        }

        arenaConfig.getConfig().set(arenaName + ".display-name", "&c" + arenaName);
        arenaConfig.saveConfig();
        arenas.add(new Arena(arenaName, "&c" + arenaName));

        player.sendMessage(ChatColor.GREEN + arenaName + " created! Use, '/arena addarena' " + arenaName + " to create a usable arena.");
    }

    public void addUsableArena(Player player, String arenaName) {
        Arena arena = getArena(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + arenaName + " not found!");
            return;
        }

        arena.setTotal(arena.getTotal()+1);
        arena.getIds().put(arena.getTotal(), arena.new SpawnPoints());
        arenaConfig.getConfig().set(arenaName + ".ids." + arena.getTotal(), "");
        arenaConfig.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Usable arena created! ID: " + arena.getTotal() + ".");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "To finish completion use, '/arena setpos1|setpos2 " + arenaName + " " + arena.getTotal() + "'");
    }

    public void setSpawnPoint(Player player, Arena arena, int id, int pos) {
        arena.setPosition(id, pos, player.getLocation());
        arenaConfig.getConfig().set(arena.getArenaName() + ".ids." + id + ".pos" + pos, MiscUtils.serializeLocation(player.getLocation()));
        arenaConfig.saveConfig();

        player.sendMessage(ChatColor.GREEN + "You set spawn point " + pos + " of " + arena.getArenaName() + ":" + id + "!");
        player.sendMessage("");
        Arena.SpawnPoints spawnPoints = arena.getSpawnPoints(id);
        if (spawnPoints.getPos1() == null || spawnPoints.getPos2() == null) {
            player.sendMessage(ChatColor.RED + "1/2 spawn points set!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "2/2 spawn points set!");
    }

    public void setKit(Player player) {
        this.kitItems = player.getInventory().getContents();
        this.armorItems = player.getInventory().getArmorContents();

        mainConfig.getConfig().set("Kit.items", kitItems);
        mainConfig.getConfig().set("Kit.armor", armorItems);
        mainConfig.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Your current inventory setup is now the kit!");
    }
}
