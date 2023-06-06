package org.plexforge.customizablespawns.listeners;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PlayerJoinListener implements Listener {

    FileConfiguration config;

    public PlayerJoinListener(FileConfiguration configuration){
        this.config = configuration;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        ConfigurationSection spawnLocations = config.getConfigurationSection("spawnLocations." + player.getWorld().getName());

        if(spawnLocations != null){
            Set<String> spawnKeys = spawnLocations.getKeys(false);
            List<String> spawnList = new ArrayList<>(spawnKeys);

            String randomSpawnKey = spawnList.get(new Random().nextInt(spawnList.size()));
            ConfigurationSection randomSpawnSection = spawnLocations.getConfigurationSection(randomSpawnKey);

            if(randomSpawnSection != null){
                int x = randomSpawnSection.getInt("x");
                int y = randomSpawnSection.getInt("y");
                int z = randomSpawnSection.getInt("z");

                Location location = new Location(player.getWorld(), x, y, z);

                player.teleport(location);
            }

        }
    }

}
