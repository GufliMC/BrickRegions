package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class PlayerLocalitiesEvent extends Event implements Cancellable {

    private final Player player;
    private final Collection<Locality> localities;

    private boolean cancelled = false;

    public PlayerLocalitiesEvent(Player player, Collection<Locality> localities) {
        this.player = player;
        this.localities = localities;
    }

    public Player player() {
        return player;
    }

    public Collection<Locality> localities() {
        return localities;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
