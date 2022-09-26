package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsEntityBreakEvent;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsEntityPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;

public class EntityBuildListener implements Listener {

    private void entityPlace(Player player, Entity entity, Cancellable e) {
        Location loc = entity.getLocation();
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(loc));
        if (regions.isEmpty()) {
            return;
        }

        PlayerRegionsEntityPlaceEvent event = new PlayerRegionsEntityPlaceEvent(player, regions, entity);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    private void entityBreak(Player player, Entity entity, Cancellable e) {
        Location loc = entity.getLocation();
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(loc));
        if (regions.isEmpty()) {
            return;
        }

        PlayerRegionsEntityBreakEvent event = new PlayerRegionsEntityBreakEvent(player, regions, entity);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(EntityPlaceEvent e) {
        entityPlace(e.getPlayer(), e.getEntity(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(HangingPlaceEvent e) {
        entityPlace(e.getPlayer(), e.getEntity(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityBreak(EntityDamageByEntityEvent e) {
        if ( e.getDamager() instanceof Player p && e.getEntity() instanceof ArmorStand ) {
            entityBreak(p, e.getEntity(), e);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityBreak(HangingBreakByEntityEvent e) {
        if ( e.getRemover() instanceof Player p ) {
            entityBreak(p, e.getEntity(), e);
        }
    }

}
