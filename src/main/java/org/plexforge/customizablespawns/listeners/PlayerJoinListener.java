package org.plexforge.customizablespawns.listeners;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class PlayerJoinListener implements Listener {


    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = SpawnSetListener.spawnLocationsSave.get(new Random().nextInt(SpawnSetListener.spawnLocationsSave.size()));
        player.teleport(location);
        player.sendMessage(String.valueOf(SpawnSetListener.spawnLocationsSave.size()));

    }
}
