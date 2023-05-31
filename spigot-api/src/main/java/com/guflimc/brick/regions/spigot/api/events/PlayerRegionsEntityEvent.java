package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.region.Region;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class PlayerRegionsEntityEvent extends PlayerRegionsEvent {

    private final Entity entity;

    public PlayerRegionsEntityEvent(Player player, Collection<Region> regions, Entity entity) {
        super(player, regions);
        this.entity = entity;
    }

    public Entity entity() {
        return entity;
    }

}
