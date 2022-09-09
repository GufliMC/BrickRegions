package com.guflimc.brick.regions.spigot.protection.listeners;

import com.gufli.kingdomcraft.premium.api.modules.regionprotection.RuleType;
import com.gufli.kingdomcraft.premium.bukkit.modules.regionprotection.RegionProtectionModule;
import com.guflimc.brick.maths.database.api.LocationConverter;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.protection.events.RegionProtectionBuildEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class BuildListener implements Listener {

    private static final List<Predicate<String>> placeableEntities = Arrays.asList(
            (s) -> s.contains("MINECART"),
            (s) -> s.contains("BOAT"),
            (s) -> Arrays.asList(
                    "ARMORSTAND",
                    "ARMOR_STAND",
                    "ITEM_FRAME"
            ).contains(s)
    );

    private void call(Player player, Block block, Cancellable e) {
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(block.getLocation()));
        if ( regions.isEmpty() ) {
            return;
        }

        RegionProtectionBuildEvent event = new RegionProtectionBuildEvent(player, regions, block);
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

    // ENTITIES

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (e.getPlayer().getItemInHand() == null) {
            return;
        }

        String type = e.getPlayer().getItemInHand().getType().name();
        if (placeableEntities.stream().noneMatch((pre) -> pre.test(type))) {
            return;
        }

        if (module.isAllowed(e.getClickedBlock().getLocation(), e.getPlayer(), RuleType.BUILD, type)) {
            return;
        }

        module.kdcp.getMessages().send(module.kdc.getPlayer(e.getPlayer().getUniqueId()), "regionsProtectBuild");
        e.setCancelled(true);
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
