package com.guflimc.brick.regions.spigot.api.events;


import com.guflimc.brick.regions.api.rules.Rule;

public class PlayerRegionsDenyByRuleEvent extends PlayerRegionsEvent {

    private final Rule rule;
    private final PlayerRegionsEvent source;

    public PlayerRegionsDenyByRuleEvent(PlayerRegionsEvent source, Rule rule) {
        super(source.player(), source.regions());
        this.rule = rule;
        this.source = source;
    }

    public PlayerRegionsEvent source() {
        return source;
    }

    public Rule rule() {
        return rule;
    }

}
