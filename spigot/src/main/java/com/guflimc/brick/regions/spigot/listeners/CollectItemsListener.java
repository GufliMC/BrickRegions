package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerLocalitiesCollectItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Collection;

public class CollectItemsListener implements Listener {

    private void collectItem(Player player, Item entity, Cancellable e) {
        Collection<Locality> regions = RegionAPI.get().localitiesAt(SpigotMath.toBrickLocation(entity.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerLocalitiesCollectItemEvent event = new PlayerLocalitiesCollectItemEvent(player, regions, entity);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }

        collectItem(player, e.getItem(), e);
    }


}
