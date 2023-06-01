package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsCollectItemEvent extends PlayerRegionsEntityEvent {

    public PlayerRegionsCollectItemEvent(Player player, Collection<Region> regions, Item entity) {
        super(player, regions, entity);
    }

    @Override
    public Item entity() {
        return (Item) super.entity();
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
