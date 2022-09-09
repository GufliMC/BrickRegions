package com.guflimc.brick.regions.spigot.protection.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RegionProtectionEntityPlaceEvent extends RegionProtectionEvent {

    private final Location location;
    private final Entity entity;

    public RegionProtectionEntityPlaceEvent(Player player, Collection<Region> regions, Location location, Entity entity) {
        super(player, regions);
        this.location = location;
        this.entity = entity;
    }

    public Location location() {
        return location;
    }

    public Entity entity() {
        return entity;
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
