package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    private boolean shouldCancel(PlayerRegionsEvent event) {
        return event.result() == PlayerRegionsEvent.Result.NEUTRAL
                && event.rule().map(r -> r.status() == RuleStatus.DENY).orElse(true);
    }

    // BLOCK BUILD

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(PlayerRegionsBlockBreakEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(PlayerRegionsBlockPlaceEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // ENTITY BUILD (painting, item frame, armor stand...)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityBreak(PlayerRegionsEntityBreakEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(PlayerRegionsEntityPlaceEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // INTERACT

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerRegionsBlockInteractEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerRegionsEntityInteractEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // CONTAINER (blocks & entities)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onContainerOpen(PlayerRegionsContainerOpenEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // HOSTILE & NEUTRAL MOBS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(PlayerRegionsEntityDamageEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message (check for neutral or hostile mob)
        }
    }

    // COLLECT ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCollectItem(PlayerRegionsCollectItemEvent event) {
        if (shouldCancel(event)) {
            event.setCancelled(true);
            // TODO message
        }
    }

}
