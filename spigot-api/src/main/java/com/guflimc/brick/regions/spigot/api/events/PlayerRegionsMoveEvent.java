package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsMoveEvent extends Event {

    private final Player player;

    private final Collection<Locality> from;
    private final Collection<Locality> to;

    private final Collection<Locality> uniqueFrom;
    private final Collection<Locality> uniqueTo;

    public PlayerRegionsMoveEvent(Player player, Collection<Locality> from, Collection<Locality> to, Collection<Locality> uniqueFrom, Collection<Locality> uniqueTo) {
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

    public Collection<Locality> from() {
        return from;
    }

    public Collection<Locality> to() {
        return to;
    }

    public Collection<Locality> uniqueFrom() {
        return uniqueFrom;
    }

    public Collection<Locality> uniqueTo() {
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
