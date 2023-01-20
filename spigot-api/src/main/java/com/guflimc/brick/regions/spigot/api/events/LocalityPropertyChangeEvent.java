package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LocalityPropertyChangeEvent extends LocalityEvent {

    public LocalityPropertyChangeEvent(Locality locality, boolean async) {
        super(locality, async);
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
