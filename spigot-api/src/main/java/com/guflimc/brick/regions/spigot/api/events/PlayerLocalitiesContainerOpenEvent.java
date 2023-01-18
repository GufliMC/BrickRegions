package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerLocalitiesContainerOpenEvent extends PlayerLocalitiesEvent {

    private final Inventory inventory;

    public PlayerLocalitiesContainerOpenEvent(Player player, Collection<Locality> regions, Inventory inventory) {
        super(player, regions);
        this.inventory = inventory;
    }

    public Inventory entity() {
        return inventory;
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
