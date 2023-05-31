package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.region.Region;
import org.bukkit.entity.Player;

public abstract class PlayerRegionEvent extends RegionEvent {

    private final Player player;

    public PlayerRegionEvent(Region region, Player player) {
        super(region);
        this.player = player;
    }

    public PlayerRegionEvent(Region region, Player player, boolean async) {
        super(region, async);
        this.player = player;
    }

    public Player player() {
        return player;
    }
}
