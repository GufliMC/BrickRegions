package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerLocalitiesBlockBreakEvent extends PlayerLocalitiesBlockEvent {


    public PlayerLocalitiesBlockBreakEvent(Player player, Collection<Locality> regions, Block block) {
        super(player, regions, block);
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
