package com.guflimc.brick.regions.spigot.protection.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RegionProtectionEvent extends Event implements Cancellable {

    private boolean cancelled;
    private final Player player;
    private final Collection<Region> regions;

    public RegionProtectionEvent(Player player, Collection<Region> regions) {
        this.player = player;
        this.regions = regions;
    }

    public Player player() {
        return player;
    }

    public Collection<Region> regions() {
        return regions;
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
