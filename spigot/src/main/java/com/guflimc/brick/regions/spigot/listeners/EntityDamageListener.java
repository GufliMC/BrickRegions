package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerLocalitiesEntityDamageEvent;
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
        Collection<Locality> regions = RegionAPI.get().localitiesAt(SpigotMath.toBrickLocation(entity.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerLocalitiesEntityDamageEvent event = new PlayerLocalitiesEntityDamageEvent(player, regions, entity);
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
        Player player;
        if (attacker instanceof Player p) {
            player = p;
        } else if (attacker instanceof Projectile p && p.getShooter() instanceof Player pl ) {
            player = pl;
        } else {
            return;
        }

        entityDamage(player, e.getEntity(), e);
    }

}
