package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.attribute.RegionRule;

public class PlayerRegionsDenyByRuleEvent extends PlayerRegionsEvent {

    private final RegionRule rule;
    private final PlayerRegionsEvent source;

    public PlayerRegionsDenyByRuleEvent(PlayerRegionsEvent source, RegionRule rule) {
        super(source.player(), source.regions());
        this.rule = rule;
        this.source = source;
    }

    public PlayerRegionsEvent source() {
        return source;
    }

    public RegionRule rule() {
        return rule;
    }

}
