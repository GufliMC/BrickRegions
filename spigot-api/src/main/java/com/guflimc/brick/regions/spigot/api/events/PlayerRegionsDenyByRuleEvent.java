package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.RegionProtectionRule;

public class PlayerRegionsDenyByRuleEvent extends PlayerRegionsEvent {

    private final RegionProtectionRule rule;
    private final PlayerRegionsEvent source;

    public PlayerRegionsDenyByRuleEvent(PlayerRegionsEvent source, RegionProtectionRule rule) {
        super(source.player(), source.regions());
        this.rule = rule;
        this.source = source;
    }

    public PlayerRegionsEvent source() {
        return source;
    }

    public RegionProtectionRule rule() {
        return rule;
    }

}
