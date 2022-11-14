package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsAttackedByEntityEvent;
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
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;

public class PlayerDamageListener implements Listener {

    private void damagePlayer(Entity attacker, Player target, Cancellable e) {
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toBrickLocation(target.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerRegionsAttackedByEntityEvent event = new PlayerRegionsAttackedByEntityEvent(target, regions, attacker);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) {
            return;
        }

        handle(event.getDamager(), target, event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) {
            return;
        }

        handle(event.getCombuster(), target, event);
    }

    private void handle(Entity attacker, Player target, Cancellable event) {
        if (attacker instanceof Projectile p) {
            ProjectileSource source = p.getShooter();
            if (source instanceof Entity e) {
                attacker = e;
            }
        }

        damagePlayer(attacker, target, event);
    }

}
