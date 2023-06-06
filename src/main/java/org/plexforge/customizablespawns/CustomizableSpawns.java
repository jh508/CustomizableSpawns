package org.plexforge.customizablespawns;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.plexforge.customizablespawns.listeners.SpawnSetListener;

public final class CustomizableSpawns extends JavaPlugin {

    private static CustomizableSpawns plugin;

    public static CustomizableSpawns getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        plugin = this;

        Bukkit.getServer().getPluginManager().registerEvents(new SpawnSetListener(), this);
    }


    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e){
        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
    }
}

