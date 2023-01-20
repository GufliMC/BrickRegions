package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LocalityRuleRemoveEvent extends LocalityEvent {

    private final LocalityProtectionRule rule;

    public LocalityRuleRemoveEvent(Locality locality, boolean async, LocalityProtectionRule rule) {
        super(locality, async);
        this.rule = rule;
    }

    public LocalityProtectionRule rule() {
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
