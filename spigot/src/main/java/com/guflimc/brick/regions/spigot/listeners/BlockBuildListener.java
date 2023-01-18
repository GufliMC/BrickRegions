package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerLocalitiesBlockBreakEvent;
import com.guflimc.brick.regions.spigot.api.events.PlayerLocalitiesBlockPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Collection;

public class BlockBuildListener implements Listener {

    private void blockPlace(Player player, Block block, Cancellable e) {
        Collection<Locality> regions = RegionAPI.get().localitiesAt(SpigotMath.toBrickLocation(block.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerLocalitiesBlockPlaceEvent event = new PlayerLocalitiesBlockPlaceEvent(player, regions, block);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    private void blockBreak(Player player, Block block, Cancellable e) {
        Collection<Locality> regions = RegionAPI.get().localitiesAt(SpigotMath.toBrickLocation(block.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerLocalitiesBlockBreakEvent event = new PlayerLocalitiesBlockBreakEvent(player, regions, block);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        blockBreak(e.getPlayer(), e.getBlock(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        blockPlace(e.getPlayer(), e.getBlock(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        blockBreak(e.getPlayer(), e.getBlock(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent e) {
        blockPlace(e.getPlayer(), e.getBlock(), e);
    }

}
