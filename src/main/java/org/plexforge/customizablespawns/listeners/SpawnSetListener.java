package org.plexforge.customizablespawns.listeners;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.plexforge.customizablespawns.CustomizableSpawns;

import java.util.Set;

public class SpawnSetListener implements Listener {
    FileConfiguration config = CustomizableSpawns.getPlugin().getConfig();

    @EventHandler
    public boolean isSpawnSet(BlockPlaceEvent event){
        String topMaterial = CustomizableSpawns.getPlugin().getConfig().getString("topMaterial");
        String bottomMaterial = CustomizableSpawns.getPlugin().getConfig().getString("bottomMaterial");

        if(event.getBlock().getType() != Material.valueOf(topMaterial)){
            return false;
        }

        if(event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.valueOf(bottomMaterial)) && !doesSpawnExit(event)){
            ConfigurationSection spawnLocationsConfig = config.getConfigurationSection("spawnLocations");
            String worldName = event.getPlayer().getWorld().getName();

            if(spawnLocationsConfig == null){
                 spawnLocationsConfig = config.createSection("spawnLocations");
            }

            ConfigurationSection worldSection = spawnLocationsConfig.getConfigurationSection(worldName);

            if(worldSection == null){
                worldSection = spawnLocationsConfig.createSection(event.getPlayer().getWorld().getName());
            }
            Block block = event.getBlock();

            int spawnIndex = getNextSpawnIndex(worldSection);
            ConfigurationSection spawnLocation = worldSection.createSection("spawn" + spawnIndex);

            spawnLocation.set("x", block.getX());
            spawnLocation.set("y", block.getY());
            spawnLocation.set("z", block.getZ());


            CustomizableSpawns.getPlugin().saveConfig();
            event.getPlayer().sendMessage("Spawn point set!");
        }
        return true;
    }

    public boolean doesSpawnExit(BlockPlaceEvent event){
        String worldName = event.getPlayer().getWorld().getName();
        ConfigurationSection spawnLocationsConfig = config.getConfigurationSection("spawnLocations." + worldName);
        if(spawnLocationsConfig != null){
            for(String spawnKey : spawnLocationsConfig.getKeys(false)){
                ConfigurationSection spawnLocation = spawnLocationsConfig.getConfigurationSection(spawnKey);
                int x = spawnLocation.getInt("x");
                int y = spawnLocation.getInt("y");
                int z = spawnLocation.getInt("z");

                Location existingSpawn = new Location(event.getBlock().getWorld(), x, y, z);

                if(existingSpawn.equals(event.getBlock().getLocation())){
                    event.getPlayer().sendMessage("Spawn Point exists!");
                    return true;
                }
            }
        }
        event.getPlayer().sendMessage("Spawn Point does not exist!");
        return false;
    }

    private int getNextSpawnIndex(ConfigurationSection worldSection) {
        int nextIndex = 1;
        Set<String> keys = worldSection.getKeys(false);
        for (String key : keys) {
            if (key.startsWith("spawn")) {
                int index = Integer.parseInt(key.substring(5));
                if (index >= nextIndex) {
                    nextIndex = index + 1;
                }
            }
        }
        return nextIndex;
    }
}
