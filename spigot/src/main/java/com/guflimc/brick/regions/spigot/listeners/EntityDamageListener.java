package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsEntityDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;

import java.util.Collection;

public class EntityDamageListener implements Listener {

    private void entityDamage(Player player, Entity entity, Cancellable e) {
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toBrickLocation(entity.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        RuleType type = null;
        if ( entity instanceof Monster ) {
            type = RuleType.ATTACK_HOSTILE_MOBS;
        } else if ( entity instanceof Animals) {
            type = RuleType.ATTACK_NEUTRAL_MOBS;
        }

        PlayerRegionsEntityDamageEvent event = new PlayerRegionsEntityDamageEvent(player, regions, entity, type);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityHit(EntityDamageByEntityEvent e) {
        if ( e.getEntity() instanceof ArmorStand || e.getEntity() instanceof Hanging ) {
            return;
        }

        attackEntityEvent(e, e.getDamager());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
        attackEntityEvent(e, e.getCombuster());
    }

    private <T extends EntityEvent & Cancellable> void attackEntityEvent(T e, Entity attacker) {
        if (e.getEntity() instanceof Player) {
            return;
        }

        Player player;
        if (attacker instanceof Player) {
            player = (Player) attacker;
        } else if (attacker instanceof Projectile && ((Projectile) attacker).getShooter() instanceof Player) {
            player = (Player) ((Projectile) attacker).getShooter();
        } else {
            return;
        }

        entityDamage(player, e.getEntity(), e);
    }

}