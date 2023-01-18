package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    private boolean shouldCancel(PlayerLocalitiesEvent event, RuleType type) {
        LocalityProtectionRule rule = LocalityProtectionRule.match(event.player(), type, event.localities()).orElse(null);
        if (rule == null || rule.status() == RuleStatus.ALLOW) {
            return false; // DEFAULT ALLOWED
        }

        PlayerLocalitiesDenyByRuleEvent denyEvent = new PlayerLocalitiesDenyByRuleEvent(event, rule);
        Bukkit.getServer().getPluginManager().callEvent(denyEvent);
        return !denyEvent.isCancelled();
    }

    // BLOCK BUILD

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(PlayerLocalitiesBlockBreakEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(PlayerLocalitiesBlockPlaceEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // ENTITY BUILD (painting, item frame, armor stand...)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityBreak(PlayerLocalitiesEntityBreakEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityPlace(PlayerLocalitiesEntityPlaceEvent event) {
        if (shouldCancel(event, RuleType.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // INTERACT

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockInteract(PlayerLocalitiesBlockInteractEvent event) {
        if (shouldCancel(event, RuleType.INTERACT)) {
            event.setCancelled(true);

            Material type = event.block().getType();
            if (type.name().contains("PRESSURE_PLATE")
                    || type.name().contains("TRIPWIRE") ) {
                return;
            }

            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerLocalitiesEntityInteractEvent event) {
        if (shouldCancel(event, RuleType.INTERACT)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    // CONTAINER (blocks & entities)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onContainerOpen(PlayerLocalitiesContainerOpenEvent event) {
        if (shouldCancel(event, RuleType.CONTAINER)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.container");
        }
    }

    // HOSTILE & NEUTRAL MOBS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(PlayerLocalitiesEntityDamageEvent event) {
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
    public void onCollectItem(PlayerLocalitiesCollectItemEvent event) {
        if (shouldCancel(event, RuleType.COLLECT_ITEMS)) {
            event.setCancelled(true);
        }
    }

    // DROP ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDropItem(PlayerLocalitiesDropItemEvent event) {
        if (shouldCancel(event, RuleType.DROP_ITEMS)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.drop_items");
        }
    }

    // PVP

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDamage(PlayerLocalitiesEntityDamageEvent event) {
        if (!(event.entity() instanceof Player)) {
            return;
        }
        if (shouldCancel(event, RuleType.PVP)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.pvp");
        }
    }

}
