package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.locality.LocalityRule;

public class PlayerRegionsDenyByRuleEvent extends PlayerRegionsEvent {

    private final LocalityRule rule;
    private final PlayerRegionsEvent source;

    public PlayerRegionsDenyByRuleEvent(PlayerRegionsEvent source, LocalityRule rule) {
        super(source.player(), source.regions());
        this.rule = rule;
        this.source = source;
    }

    public PlayerRegionsEvent source() {
        return source;
    }

    public LocalityRule rule() {
        return rule;
    }

}
