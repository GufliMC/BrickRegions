package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionAttributeRemoveEvent<T> extends RegionEvent {

    private final RegionAttributeKey<T> key;
    private final T previousValue;

    public RegionAttributeRemoveEvent(Region region, boolean async, RegionAttributeKey<T> key, T previousValue) {
        super(region, async);
        this.key = key;
        this.previousValue = previousValue;
    }

    public RegionAttributeKey<T> key() {
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
