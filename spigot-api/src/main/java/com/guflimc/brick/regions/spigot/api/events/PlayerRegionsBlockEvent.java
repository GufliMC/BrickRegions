package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class PlayerRegionsBlockEvent extends PlayerRegionsEvent {

    private final Block block;

    public PlayerRegionsBlockEvent(Player player, Collection<Region> regions, Block block) {
        super(player, regions);
        this.block = block;
    }


    public Block block() {
        return block;
    }

    //

}
