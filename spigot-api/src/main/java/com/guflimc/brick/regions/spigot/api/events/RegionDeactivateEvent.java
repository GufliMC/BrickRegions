package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionDeactivateEvent extends RegionEvent {

    public RegionDeactivateEvent(Region.Activateable region, boolean async) {
        super(region, async);
    }

    @Override
    public Region.Activateable region() {
        return (Region.Activateable) super.region();
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
