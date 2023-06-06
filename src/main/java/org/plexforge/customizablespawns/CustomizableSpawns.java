package org.plexforge.customizablespawns;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.plexforge.customizablespawns.listeners.SpawnSetListener;

public final class CustomizableSpawns extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnSetListener(getConfig(), this), this);
    }
}

