package com.guflimc.brick.regions.spigot.events;

import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerRegionLeaveEvent extends Event {

    private final Player player;
    private final Region region;

    public PlayerRegionLeaveEvent(Player player, Region region) {
        this.player = player;
        this.region = region;
    }

    public Player player() {
        return player;
    }

    public Region region() {
        return region;
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
