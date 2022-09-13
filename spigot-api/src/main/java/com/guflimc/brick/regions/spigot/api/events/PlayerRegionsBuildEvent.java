package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsBuildEvent extends PlayerRegionsEvent {

    private final Block block;

    public PlayerRegionsBuildEvent(Player player, Collection<Region> regions, Block block) {
        super(player, regions);
        this.block = block;
    }

    public Block block() {
        return block;
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
