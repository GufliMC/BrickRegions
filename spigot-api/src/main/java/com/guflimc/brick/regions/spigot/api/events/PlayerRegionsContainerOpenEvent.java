package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsContainerOpenEvent extends PlayerRegionsEvent {

    private final Inventory inventory;

    public PlayerRegionsContainerOpenEvent(Player player, Collection<Region> regions, Inventory inventory) {
        super(player, regions, RuleType.CONTAINER);
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
