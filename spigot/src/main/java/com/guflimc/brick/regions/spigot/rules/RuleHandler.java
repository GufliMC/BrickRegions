package com.guflimc.brick.regions.spigot.rules;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.action.RuleActionBase;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
import com.guflimc.brick.regions.common.rules.attributes.BaseRuleAction;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.brick.regions.spigot.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RuleHandler implements Listener {

    private boolean shouldCancel(PlayerRegionsEvent event, RuleAction action) {
        Rule rule = SpigotRegionAPI.get().rules().match(event.player(), event.regions(), action).orElse(null);
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
        if (shouldCancel(event, SpigotRegionAPI.get().rules().action())) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(PlayerRegionsBlockPlaceEvent event) {
        if (shouldCancel(event, RuleActionBase.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // ENTITY BUILD (painting, item frame, armor stand...)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityBreak(PlayerRegionsEntityBreakEvent event) {
        if (shouldCancel(event, RuleActionBase.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityPlace(PlayerRegionsEntityPlaceEvent event) {
        if (shouldCancel(event, RuleActionBase.BUILD)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.build");
        }
    }

    // INTERACT

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockInteract(PlayerRegionsBlockInteractEvent event) {
        if (shouldCancel(event, RuleActionBase.INTERACT)) {
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
    public void onEntityInteract(PlayerRegionsEntityInteractEvent event) {
        if (shouldCancel(event, RuleActionBase.INTERACT)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.interact");
        }
    }

    // CONTAINER (blocks & entities)

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onContainerOpen(PlayerRegionsContainerOpenEvent event) {
        if (shouldCancel(event, RuleActionBase.CONTAINER)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.container");
        }
    }

    // HOSTILE & NEUTRAL MOBS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(PlayerRegionsEntityDamageEvent event) {
        if (event.entity() instanceof Monster) {
            if (shouldCancel(event, RuleActionBase.ATTACK_HOSTILE_MOBS)) {
                event.setCancelled(true);
                SpigotI18nAPI.get(this).send(event.player(), "protection.attack_hostile_mobs");
            }
            return;
        }

        if (shouldCancel(event, RuleActionBase.ATTACK_NEUTRAL_MOBS)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.attack_neutral_mobs");
        }
    }

    // COLLECT ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCollectItem(PlayerRegionsCollectItemEvent event) {
        if (shouldCancel(event, RuleActionBase.COLLECT_ITEMS)) {
            event.setCancelled(true);
        }
    }

    // DROP ITEMS

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDropItem(PlayerRegionsDropItemEvent event) {
        if (shouldCancel(event, RuleActionBase.DROP_ITEMS)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.drop_items");
        }
    }

    // PVP

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDamage(PlayerRegionsEntityDamageEvent event) {
        if (!(event.entity() instanceof Player)) {
            return;
        }
        if (shouldCancel(event, RuleActionBase.PVP)) {
            event.setCancelled(true);
            SpigotI18nAPI.get(this).send(event.player(), "protection.pvp");
        }
    }

}
