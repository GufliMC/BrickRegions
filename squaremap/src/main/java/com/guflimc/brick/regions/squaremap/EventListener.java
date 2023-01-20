package com.guflimc.brick.regions.squaremap;

import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.ShapeRegion;
import com.guflimc.brick.regions.api.domain.TileRegion;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class EventListener implements Listener {

    private final SquaremapRenderer renderer;

    public EventListener(SquaremapRenderer renderer) {
        this.renderer = renderer;
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        renderer.render(event.getWorld());
    }

    @EventHandler
    public void onUnload(WorldUnloadEvent event) {
        renderer.clear(event.getWorld());
    }

    // TODO only render single region instead of rerendering the whole world since this is very inefficient
    // TODO also listen for locality update event, for example when the color attribute is changed.

    @EventHandler
    public void onRegionCreate(RegionCreateEvent event) {
        render(event.region());
    }

    @EventHandler
    public void onRegionCreate(RegionDeleteEvent event) {
        render(event.region());
    }

    @EventHandler
    public void onRegister(RegionRegisterEvent event) {
        render(event.region());
    }

    @EventHandler
    public void onUnregister(RegionUnregisterEvent event) {
        render(event.region());
    }

    @EventHandler
    public void onAttributeChange(LocalityAttributeChangeEvent<?> event) {
        render(event.locality());
    }

    @EventHandler
    public void onAttributeRemove(LocalityAttributeRemoveEvent<?> event) {
        render(event.locality());
    }

    private void render(Locality locality) {
        if (!(locality instanceof ShapeRegion) && !(locality instanceof TileRegion)) {
            return;
        }

        renderer.render(locality.worldId());
    }
}
