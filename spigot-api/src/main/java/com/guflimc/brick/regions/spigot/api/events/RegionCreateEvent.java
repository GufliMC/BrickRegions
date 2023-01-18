package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionCreateEvent extends RegionEvent {

    public RegionCreateEvent(Region region) {
        super(region, true);
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
