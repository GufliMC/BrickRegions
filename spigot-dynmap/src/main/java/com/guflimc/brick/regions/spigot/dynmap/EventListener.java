package com.guflimc.brick.regions.spigot.dynmap;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventListener implements Listener {

    private final JavaPlugin plugin;
    private final DynmapRenderer renderer;

    public EventListener(JavaPlugin plugin, DynmapRenderer renderer) {
        this.plugin = plugin;
        this.renderer = renderer;

        timer();
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        renderer.render(event.getWorld());
    }

    @EventHandler
    public void onUnload(WorldUnloadEvent event) {
        renderer.clear(event.getWorld());
    }

    @EventHandler
    public void onRegionCreate(RegionCreateEvent event) {
        render(event.region());
    }

    @EventHandler
    public void onRegionDelete(RegionDeleteEvent event) {
        renderer.delete(event.region());
    }

    @EventHandler
    public void onRegister(RegionRegisterEvent event) {
        render(event.region());
    }

    @EventHandler
    public void onUnregister(RegionUnregisterEvent event) {
        renderer.delete(event.region());
    }

    @EventHandler
    public void onAttributeChange(RegionAttributeChangeEvent<?> event) {
        render(event.region());
    }

    @EventHandler
    public void onAttributeRemove(RegionAttributeRemoveEvent<?> event) {
        render(event.region());
    }

    @EventHandler
    public void onSave(RegionSaveEvent event) {
        render(event.region());
    }

    //

    private final Map<Region, Instant> queue = new ConcurrentHashMap<>();

    private void render(Region region) {
        queue.put(region, Instant.now());
    }

    private void timer() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Instant t = Instant.now().minus(1, ChronoUnit.SECONDS);
            Set.copyOf(queue.keySet()).stream()
                    .filter(loc -> queue.get(loc).isBefore(t))
                    .forEach(loc -> {
                        renderer.render(loc);
                        queue.remove(loc);
                    });
        }, 20L, 20L);
    }
}
