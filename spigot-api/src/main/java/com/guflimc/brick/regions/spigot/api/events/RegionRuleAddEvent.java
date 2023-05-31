package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.locality.LocalityRule;
import com.guflimc.brick.regions.api.domain.region.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionRuleAddEvent extends RegionEvent {

    private final LocalityRule rule;

    public RegionRuleAddEvent(Region region, boolean async, LocalityRule rule) {
        super(region, async);
        this.rule = rule;
    }

    public LocalityRule rule() {
        return rule;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
