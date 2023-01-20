package com.guflimc.brick.regions.squaremap;

import com.guflimc.brick.regions.api.domain.ShapeRegion;
import com.guflimc.brick.regions.api.domain.TileRegion;
import com.guflimc.brick.regions.spigot.api.events.RegionCreateEvent;
import com.guflimc.brick.regions.spigot.api.events.RegionDeleteEvent;
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
        if ( !(event.region() instanceof ShapeRegion) && !(event.region() instanceof TileRegion) ) {
            return;
        }

        renderer.render(event.region().worldId());
    }

    @EventHandler
    public void onRegionCreate(RegionDeleteEvent event) {
        if ( !(event.region() instanceof ShapeRegion) && !(event.region() instanceof TileRegion) ) {
            return;
        }

        renderer.render(event.region().worldId());
    }
}
