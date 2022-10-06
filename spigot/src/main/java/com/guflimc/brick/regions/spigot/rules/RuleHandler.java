package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    private boolean shouldCancel(PlayerRegionsEvent event) {
        return event.result() == PlayerRegionsEvent.Result.NEUTRAL
                && event.rule().map(r -> r.status() != RuleStatus.ALLOW).orElse(true);
    }

    // BLOCK BUILD

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(PlayerRegionsBlockBreakEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(PlayerRegionsBlockPlaceEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // ENTITY BUILD (painting, item frame, armor stand...)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityBreak(PlayerRegionsEntityBreakEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(PlayerRegionsEntityPlaceEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // INTERACT

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerRegionsBlockInteractEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerRegionsEntityInteractEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    // CONTAINER (blocks & entities)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onContainerOpen(PlayerRegionsContainerOpenEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.container");
        }
    }

    // HOSTILE & NEUTRAL MOBS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(PlayerRegionsEntityDamageEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            if ( event.entity() instanceof Monster ) {
                SpigotI18nAPI.get(this).send(event.player(), "protection.attack_hostile_mobs");
            } else {
                SpigotI18nAPI.get(this).send(event.player(), "protection.attack_neutral_mobs");
            }
        }
    }

    // COLLECT ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCollectItem(PlayerRegionsCollectItemEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
        }
    }

}
