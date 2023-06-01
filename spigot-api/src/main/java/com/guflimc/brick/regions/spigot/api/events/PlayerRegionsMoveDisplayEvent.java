package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerRegionsMoveDisplayEvent extends Event {

    private final Player player;
    private final Collection<Region> from;
    private final Collection<Region> to;

    private Component title;
    private Component subtitle;
    private Component actionbar;

    public PlayerRegionsMoveDisplayEvent(Player player, Collection<Region> from, Collection<Region> to,
                                         Component title, Component subtitle, Component actionbar) {
        super(true);
        this.player = player;
        this.from = from;
        this.to = to;

        this.title = title;
        this.subtitle = subtitle;
        this.actionbar = actionbar;
    }

    public Player player() {
        return player;
    }

    public Collection<Region> from() {
        return from;
    }

    public Collection<Region> to() {
        return to;
    }

    public Component title() {
        return title;
    }

    public Component subtitle() {
        return subtitle;
    }

    public Component actionbar() {
        return actionbar;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public void setSubtitle(Component subtitle) {
        this.subtitle = subtitle;
    }

    public void setActionbar(Component actionbar) {
        this.actionbar = actionbar;
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
