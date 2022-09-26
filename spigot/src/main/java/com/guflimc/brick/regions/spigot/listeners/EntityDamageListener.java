package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsEntityDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

        PlayerRegionsEntityDamageEvent event = new PlayerRegionsEntityDamageEvent(player, regions, entity);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityHit(EntityDamageByEntityEvent e) {
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
