package com.ma4z.unstablesmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class UnStableSMP extends JavaPlugin implements Listener, TabExecutor {

    private final Set<String> deadPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("revive").setExecutor(this);
        getLogger().info("UnStableSMP Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("UnStableSMP Plugin Disabled! Goodbye");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String name = player.getName().toLowerCase();
        deadPlayers.add(name);
        Bukkit.getScheduler().runTaskLater(this, () -> player.kickPlayer(ChatColor.RED + "You died!"), 1L);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName().toLowerCase();
        if (deadPlayers.contains(name)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "You died!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("revive")) {
            if (!sender.hasPermission("unstablesmp.revive")) {
                sender.sendMessage(ChatColor.RED + "You don’t have permission to use this command.");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /revive <player>");
                return true;
            }

            String target = args[0].toLowerCase();
            if (deadPlayers.remove(target)) {
                sender.sendMessage(ChatColor.GREEN + "Revived player: " + args[0]);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not marked as dead.");
                return true;
            }
        }
        return false;
    }
}
