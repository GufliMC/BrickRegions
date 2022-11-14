package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.domain.RegionProtectionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    private boolean shouldCancel(PlayerRegionsEvent event, RuleType type) {
        RegionProtectionRule rule = RegionProtectionRule.match(event.player(), type, event.regions()).orElse(null);
        if (rule == null || rule.status() == RuleStatus.ALLOW) {
            return false; // DEFAULT ALLOWED
        }

        PlayerRegionsDenyByRuleEvent denyEvent = new PlayerRegionsDenyByRuleEvent(event, rule);
        Bukkit.getServer().getPluginManager().callEvent(denyEvent);
        return !denyEvent.isCancelled();
    }

    // BLOCK BUILD

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(PlayerRegionsBlockBreakEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(PlayerRegionsBlockPlaceEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // ENTITY BUILD (painting, item frame, armor stand...)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityBreak(PlayerRegionsEntityBreakEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityPlace(PlayerRegionsEntityPlaceEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // INTERACT

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockInteract(PlayerRegionsBlockInteractEvent event) {
        if (shouldCancel(event, RuleType.INTERACT)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerRegionsEntityInteractEvent event) {
        if (shouldCancel(event, RuleType.INTERACT)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    // CONTAINER (blocks & entities)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onContainerOpen(PlayerRegionsContainerOpenEvent event) {
        if (shouldCancel(event, RuleType.CONTAINER)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.container");
        }
    }

    // HOSTILE & NEUTRAL MOBS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(PlayerRegionsEntityDamageEvent event) {
        if (event.entity() instanceof Monster) {
            if (shouldCancel(event, RuleType.ATTACK_HOSTILE_MOBS)) {
                event.setCancelled(true);
                SpigotI18nAPI.get(this).send(event.player(), "protection.attack_hostile_mobs");
            }
            return;
        }

        if (shouldCancel(event, RuleType.ATTACK_NEUTRAL_MOBS)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.attack_neutral_mobs");
        }
    }

    // COLLECT ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCollectItem(PlayerRegionsCollectItemEvent event) {
        if (shouldCancel(event, RuleType.COLLECT_ITEMS)) {
            event.setCancelled(true);
        }
    }

    // DROP ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDropItem(PlayerRegionsDropItemEvent event) {
        if (shouldCancel(event, RuleType.DROP_ITEMS)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.drop_items");
        }
    }

    // PVP

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDamage(PlayerRegionsAttackedByEntityEvent event) {
        if ( !(event.entity() instanceof Player attacker) ) {
            return;
        }
        if (shouldCancel(event, RuleType.PVP)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(attacker, "protection.pvp");
        }
    }

}
