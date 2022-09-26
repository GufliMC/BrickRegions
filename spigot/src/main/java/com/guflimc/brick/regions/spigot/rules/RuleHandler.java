package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    // BLOCK BUILD

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(PlayerRegionsBlockBreakEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.BUILD, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(PlayerRegionsBlockPlaceEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.BUILD, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // ENTITY BUILD (painting, item frame, armor stand...)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityBreak(PlayerRegionsEntityBreakEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.BUILD, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(PlayerRegionsEntityPlaceEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.BUILD, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // INTERACT

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerRegionsBlockInteractEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.INTERACT, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerRegionsEntityInteractEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.INTERACT, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // CONTAINER (blocks & entities)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onContainerOpen(PlayerRegionsContainerOpenEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.CONTAINER, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // HOSTILE & NEUTRAL MOBS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(PlayerRegionsEntityDamageEvent event) {
        if (event.entity() instanceof Monster) {
            if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.ATTACK_HOSTILE_MOBS, event.regions())) {
                event.setCancelled(true);
                // TODO message
            }
            return;
        }

        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.ATTACK_NEUTRAL_MOBS, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

    // COLLECT ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCollectItem(PlayerRegionsCollectItemEvent event) {
        if (!SpigotRegionAPI.get().isAllowed(event.player(), RuleType.COLLECT_ITEMS, event.regions())) {
            event.setCancelled(true);
            // TODO message
        }
    }

}
