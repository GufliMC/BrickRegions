package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;

public class PlayerLocalitiesDenyByRuleEvent extends PlayerLocalitiesEvent {

    private final LocalityProtectionRule rule;
    private final PlayerLocalitiesEvent source;

    public PlayerLocalitiesDenyByRuleEvent(PlayerLocalitiesEvent source, LocalityProtectionRule rule) {
        super(source.player(), source.localities());
        this.rule = rule;
        this.source = source;
    }

    public PlayerLocalitiesEvent source() {
        return source;
    }

    public LocalityProtectionRule rule() {
        return rule;
    }

}
