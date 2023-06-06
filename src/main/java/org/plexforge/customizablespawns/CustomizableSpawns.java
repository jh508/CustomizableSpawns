package org.plexforge.customizablespawns;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.plexforge.customizablespawns.listeners.PlayerJoinListener;
import org.plexforge.customizablespawns.listeners.SpawnSetListener;

public final class CustomizableSpawns extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(getConfig()), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnSetListener(getConfig(), this), this);
    }
}

