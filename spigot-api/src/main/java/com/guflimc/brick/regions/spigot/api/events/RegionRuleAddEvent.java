package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.rules.Rule;;
import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionRuleAddEvent extends RegionEvent {

    private final Rule rule;

    public RegionRuleAddEvent(Region region, boolean async, Rule rule) {
        super(region, async);
        this.rule = rule;
    }

    public Rule rule() {
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
