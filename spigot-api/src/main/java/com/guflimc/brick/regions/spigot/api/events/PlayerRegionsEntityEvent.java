package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class PlayerRegionsEntityEvent extends PlayerRegionsEvent {

    private final Entity entity;

    public PlayerRegionsEntityEvent(Player player, Collection<Region> regions, Entity entity, RuleType type) {
        super(player, regions, type);
        this.entity = entity;
    }

    public Entity entity() {
        return entity;
    }

}
