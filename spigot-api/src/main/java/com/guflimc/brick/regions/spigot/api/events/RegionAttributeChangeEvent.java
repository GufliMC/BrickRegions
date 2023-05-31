package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.locality.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.region.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


public class RegionAttributeChangeEvent<T> extends RegionEvent {

    private final LocalityAttributeKey<T> key;
    private final T previousValue;
    private final T value;

    public RegionAttributeChangeEvent(Region region, boolean async, LocalityAttributeKey<T> key, T previousValue, T value) {
        super(region, async);
        this.key = key;
        this.previousValue = previousValue;
        this.value = value;
    }

    public LocalityAttributeKey<T> key() {
        return key;
    }

    public T previousValue() {
        return previousValue;
    }

    public T value() {
        return value;
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
