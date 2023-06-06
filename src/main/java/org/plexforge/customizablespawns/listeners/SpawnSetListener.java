package org.plexforge.customizablespawns.listeners;


import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.plexforge.customizablespawns.CustomizableSpawns;

import java.util.Set;

public class SpawnSetListener implements Listener {
    FileConfiguration config;
    CustomizableSpawns plugin;

    String pluginPrefix = ChatColor.GOLD + "" + ChatColor.BOLD + "CustomizableSpawns: " + ChatColor.WHITE;

    public SpawnSetListener(FileConfiguration configFile, CustomizableSpawns pluginFile){
        this.config = configFile;
        this.plugin = pluginFile;
    }

    @EventHandler
    public boolean isSpawnSet(BlockPlaceEvent event){
        if(!event.getPlayer().hasPermission("customizable.spawn.setter")){
            return false;
        }

        String topMaterial = config.getString("topMaterial");
        String bottomMaterial = config.getString("bottomMaterial");

        if(event.getBlock().getType() != Material.valueOf(topMaterial)){
            return false;
        }

        if(event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.valueOf(bottomMaterial)) && !doesSpawnExit(event)){
            String worldName = event.getPlayer().getWorld().getName();
            ConfigurationSection spawnLocationsConfig = config.getConfigurationSection("spawnLocations");
            Location spawnBlock = event.getBlock().getRelative(BlockFace.DOWN).getLocation();

            if(spawnLocationsConfig == null){
                 spawnLocationsConfig = config.createSection("spawnLocations");
            }
            ConfigurationSection worldSection = spawnLocationsConfig.getConfigurationSection(worldName);

            if(worldSection == null){
                worldSection = spawnLocationsConfig.createSection(event.getPlayer().getWorld().getName());
            }

            int spawnIndex = getNextSpawnIndex(worldSection);
            ConfigurationSection spawnLocation = worldSection.createSection("spawn" + spawnIndex);

            spawnLocation.set("x", spawnBlock.getX());
            spawnLocation.set("y", spawnBlock.getY());
            spawnLocation.set("z", spawnBlock.getZ());


            plugin.saveConfig();
            event.getPlayer().sendMessage(pluginPrefix + "Spawn point set!");
        }
        return true;
    }

    public boolean removeSpawnBlock(BlockBreakEvent event){
        if(!event.getPlayer().hasPermission("customizable.spawn.setter") && event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
            event.setCancelled(true);
            return false;
        }

        Material eventBlockBottomMaterial;
        Material eventBlockTopMaterial;

        if(event.getBlock().getType().equals(Material.valueOf(config.getString("topMaterial")))){
             eventBlockBottomMaterial = event.getBlock().getRelative(BlockFace.DOWN).getType();
             eventBlockTopMaterial = event.getBlock().getType();
        }
        else{
            eventBlockBottomMaterial = event.getBlock().getType();
            eventBlockTopMaterial = event.getBlock().getRelative(BlockFace.UP).getType();
        }

        Material configBottomMaterial = Material.valueOf(config.getString("bottomMaterial"));
        Material configTopMaterial = Material.valueOf(config.getString("topMaterial"));

        if(eventBlockBottomMaterial.equals(configBottomMaterial) && eventBlockTopMaterial.equals(configTopMaterial) || eventBlockBottomMaterial.equals(configTopMaterial) && eventBlockTopMaterial.equals(configBottomMaterial)){
            String worldName = event.getBlock().getWorld().getName();
            ConfigurationSection spawnLocationsConfig = config.getConfigurationSection("spawnLocations." + worldName);
            if(spawnLocationsConfig != null){
                for(String spawnKey : spawnLocationsConfig.getKeys(false)){
                    ConfigurationSection spawnLocation = spawnLocationsConfig.getConfigurationSection(spawnKey);
                    int x = spawnLocation.getInt("x");
                    int y = spawnLocation.getInt("y");
                    int z = spawnLocation.getInt("z");

                    Location existingSpawn = new Location(event.getBlock().getWorld(), x, y, z);

                    if(existingSpawn.equals(event.getBlock().getLocation()) ||
                            existingSpawn.equals(event.getBlock().getRelative(BlockFace.UP).getLocation()) ||
                            existingSpawn.equals(event.getBlock().getRelative(BlockFace.DOWN).getLocation())){
                        spawnLocationsConfig.set(spawnKey, null);
                        event.getPlayer().sendMessage( pluginPrefix + "Spawn point deleted");
                        plugin.saveConfig();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean doesSpawnExit(BlockPlaceEvent event){
        String worldName = event.getPlayer().getWorld().getName();
        ConfigurationSection spawnLocationsConfig = config.getConfigurationSection("spawnLocations." + worldName);
        if(spawnLocationsConfig != null){
            for(String spawnKey : spawnLocationsConfig.getKeys(false)){
                ConfigurationSection spawnLocation = spawnLocationsConfig.getConfigurationSection(spawnKey);
                int x = spawnLocation.getInt("x");
                int y = spawnLocation.getInt("y");
                int z = spawnLocation.getInt("z");

                Location existingSpawn = new Location(event.getBlock().getWorld(), x, y, z);

                if(existingSpawn.equals(event.getBlock().getRelative(BlockFace.DOWN).getLocation())){
                    event.getPlayer().sendMessage("Spawn Point exists!");
                    return true;
                }
            }
        }
        event.getPlayer().sendMessage(pluginPrefix + "Spawn Point does not exist!");
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
