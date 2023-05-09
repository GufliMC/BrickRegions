package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TileRegionChangeEvent extends RegionEvent {

    public TileRegionChangeEvent(TileRegion region, boolean async) {
        super(region, async);
    }

    @Override
    public TileRegion region() {
        return (TileRegion) super.region();
    }

    //

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
