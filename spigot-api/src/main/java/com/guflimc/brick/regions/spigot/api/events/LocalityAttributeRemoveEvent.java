package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LocalityAttributeRemoveEvent<T> extends LocalityEvent {

    private final LocalityAttributeKey<T> key;
    private final T previousValue;

    public LocalityAttributeRemoveEvent(Locality locality, boolean async, LocalityAttributeKey<T> key, T previousValue) {
        super(locality, async);
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
