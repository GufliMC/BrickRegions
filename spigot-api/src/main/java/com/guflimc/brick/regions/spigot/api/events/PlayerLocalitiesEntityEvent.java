package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class PlayerLocalitiesEntityEvent extends PlayerLocalitiesEvent {

    private final Entity entity;

    public PlayerLocalitiesEntityEvent(Player player, Collection<Locality> regions, Entity entity) {
        super(player, regions);
        this.entity = entity;
    }

    public Entity entity() {
        return entity;
    }

}
