package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsBuildEvent;
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

public class BuildListener implements Listener {

    private void call(Player player, Block block, Cancellable e) {
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(block.getLocation()));
        if (regions.isEmpty()) {
            return;
        }

        PlayerRegionsBuildEvent event = new PlayerRegionsBuildEvent(player, regions, block);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        call(e.getPlayer(), e.getBlock(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        call(e.getPlayer(), e.getBlock(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        call(e.getPlayer(), e.getBlock(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent e) {
        call(e.getPlayer(), e.getBlock(), e);
    }

    // TODO interact
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    public void onSoilDestroy(PlayerInteractEvent e) {
//        if (e.getAction() != Action.PHYSICAL) {
//            return;
//        }
//
//        if (e.getClickedBlock() == null) {
//            return;
//        }
//
//        String type = e.getClickedBlock().getType().name();
//        if (!type.equals("SOIL") && !type.equals("FARMLAND")) {
//            return;
//        }
//
//        if (module.isAllowed(e.getClickedBlock().getLocation(), e.getPlayer(), RuleType.BUILD, type)) {
//            return;
//        }
//
//        module.kdcp.getMessages().send(module.kdc.getPlayer(e.getPlayer().getUniqueId()), "regionsProtectBuild");
//        e.setCancelled(true);
//    }


}
