package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import org.bukkit.entity.Player;

public abstract class PlayerLocalityEvent extends LocalityEvent {

    private final Player player;

    public PlayerLocalityEvent(Locality region, Player player) {
        super(region);
        this.player = player;
    }

    public PlayerLocalityEvent(Locality region, Player player, boolean async) {
        super(region, async);
        this.player = player;
    }

    public Player player() {
        return player;
    }
}
