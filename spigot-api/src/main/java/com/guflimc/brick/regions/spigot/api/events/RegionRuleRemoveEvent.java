package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.attribute.RegionRule;
import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegionRuleRemoveEvent extends RegionEvent {

    private final RegionRule rule;

    public RegionRuleRemoveEvent(Region region, boolean async, RegionRule rule) {
        super(region, async);
        this.rule = rule;
    }

    public RegionRule rule() {
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
