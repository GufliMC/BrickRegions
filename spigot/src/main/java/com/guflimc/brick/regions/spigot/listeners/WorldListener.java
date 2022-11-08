package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.regions.spigot.SpigotBrickRegionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    private final SpigotBrickRegionManager manager;

    public WorldListener(SpigotBrickRegionManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        manager.loadWorld(event.getWorld().getUID());
    }

}
