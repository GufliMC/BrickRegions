package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsEntityPlaceEvent extends PlayerRegionsEntityEvent {


    public PlayerRegionsEntityPlaceEvent(Player player, Collection<Region> regions, Entity entity) {
        super(player, regions, entity);
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
