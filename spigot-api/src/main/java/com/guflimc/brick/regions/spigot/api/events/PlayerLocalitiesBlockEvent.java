package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class PlayerLocalitiesBlockEvent extends PlayerLocalitiesEvent {

    private final Block block;

    public PlayerLocalitiesBlockEvent(Player player, Collection<Locality> regions, Block block) {
        super(player, regions);
        this.block = block;
    }


    public Block block() {
        return block;
    }

    //

}
