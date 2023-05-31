package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.region.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsMoveEvent extends Event {

    private final Player player;

    private final Collection<Region> from;
    private final Collection<Region> to;

    private final Collection<Region> uniqueFrom;
    private final Collection<Region> uniqueTo;

    public PlayerRegionsMoveEvent(Player player, Collection<Region> from, Collection<Region> to, Collection<Region> uniqueFrom, Collection<Region> uniqueTo) {
        super(true);
        this.player = player;
        this.from = from;
        this.to = to;
        this.uniqueFrom = uniqueFrom;
        this.uniqueTo = uniqueTo;
    }

    public Player player() {
        return player;
    }

    public Collection<Region> from() {
        return from;
    }

    public Collection<Region> to() {
        return to;
    }

    public Collection<Region> uniqueFrom() {
        return uniqueFrom;
    }

    public Collection<Region> uniqueTo() {
        return uniqueTo;
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
