package com.guflimc.brick.regions.spigot.api.events;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.RegionRule;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public abstract class PlayerRegionsEvent extends Event implements Cancellable {

    private Result result = Result.NEUTRAL;
    private final Player player;
    private final Collection<Region> regions;
    private final Optional<RegionRule> rule;

    public PlayerRegionsEvent(Player player, Collection<Region> regions, Optional<RegionRule> rule) {
        this.player = player;
        this.regions = regions;
        this.rule = rule;

        rule.ifPresent(System.out::println); // TODO remove this
    }

    public PlayerRegionsEvent(Player player, Collection<Region> regions, RuleType type) {
        this(player, regions, type == null ? Optional.empty() : SpigotRegionAPI.get().findRule(player, type, regions));
    }

    public Player player() {
        return player;
    }

    public Collection<Region> regions() {
        return regions;
    }

    public Optional<RegionRule> rule() {
        return rule;
    }

    public Result result() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public boolean isCancelled() {
        return result == Result.DENY;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.result = Result.DENY;
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

    //

    public enum Result {
        /**
         * Allow the event in any case, built-in rules will be ignored.
         */
        ALLOW,
        /**
         * Deny the event, you must provide feedback yourself.
         */
        DENY,
        /**
         * Neutral, built-in rules will still be checked.
         */
        NEUTRAL;
    }

}
