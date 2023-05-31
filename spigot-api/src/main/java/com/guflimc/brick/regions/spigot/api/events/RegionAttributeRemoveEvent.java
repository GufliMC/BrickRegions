package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.locality.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.region.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionAttributeRemoveEvent<T> extends RegionEvent {

    private final LocalityAttributeKey<T> key;
    private final T previousValue;

    public RegionAttributeRemoveEvent(Region region, boolean async, LocalityAttributeKey<T> key, T previousValue) {
        super(region, async);
        this.key = key;
        this.previousValue = previousValue;
    }

    public LocalityAttributeKey<T> key() {
        return key;
    }

    public T previousValue() {
        return previousValue;
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
