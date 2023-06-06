package org.plexforge.customizablespawns.listeners;


import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.plexforge.customizablespawns.CustomizableSpawns;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpawnSetListener implements Listener {
    private static FileConfiguration config;
    private final CustomizableSpawns plugin;
    private final String pluginPrefix = ChatColor.GOLD + "" + ChatColor.BOLD + "CustomizableSpawns: " + ChatColor.WHITE;

    public static List<Location> spawnLocationsSave = new ArrayList<>();


    public SpawnSetListener(FileConfiguration configFile, CustomizableSpawns pluginFile){
        config = configFile;
        this.plugin = pluginFile;
    }

    public static void loadSpawnLocations(){
        ConfigurationSection spawnLocationsConfig = config.getConfigurationSection("spawnLocations");
        if (spawnLocationsConfig != null) {
            for (String worldName : spawnLocationsConfig.getKeys(false)) {
                ConfigurationSection worldSection = spawnLocationsConfig.getConfigurationSection(worldName);
                for (String spawnKey : worldSection.getKeys(false)) {
                    ConfigurationSection spawnLocation = worldSection.getConfigurationSection(spawnKey);
                    double x = spawnLocation.getDouble("x");
                    double y = spawnLocation.getDouble("y");
                    double z = spawnLocation.getDouble("z");
                    Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
                    spawnLocationsSave.add(location);
                }
            }
        }

        for(Location location : spawnLocationsSave){
            System.out.println(location.toString());
        }

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
            spawnLocationsSave.add(spawnBlock);
            event.getPlayer().sendMessage(pluginPrefix + "Spawn point set!");
        }
        return true;
    }

    @EventHandler
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

                        for(Location location : spawnLocationsSave){
                            if(existingSpawn.equals(location)){
                                spawnLocationsSave.remove(location);
                            }
                        }
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
                for (String spawnKey : spawnLocationsConfig.getKeys(false)) {
                    ConfigurationSection spawnLocation = spawnLocationsConfig.getConfigurationSection(spawnKey);
                    if(spawnLocation != null){
                        int x = spawnLocation.getInt("x");
                        int y = spawnLocation.getInt("y");
                        int z = spawnLocation.getInt("z");

                        Location existingSpawn = new Location(event.getBlock().getWorld(), x, y, z);

                        if (existingSpawn.equals(event.getBlock().getRelative(BlockFace.DOWN).getLocation())) {
                            event.getPlayer().sendMessage("Spawn Point exists!");
                            return true;
                        }
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
