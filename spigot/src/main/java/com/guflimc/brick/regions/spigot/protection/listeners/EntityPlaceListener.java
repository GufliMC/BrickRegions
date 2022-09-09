package com.guflimc.brick.regions.spigot.protection.listeners;

import com.gufli.kingdomcraft.premium.api.modules.regionprotection.RuleType;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.protection.events.RegionProtectionBuildEvent;
import com.guflimc.brick.regions.spigot.protection.events.RegionProtectionEntityPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class EntityPlaceListener implements Listener {

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
        if (regions.isEmpty()) {
            return;
        }

        RegionProtectionBuildEvent event = new RegionProtectionBuildEvent(player, regions, block);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPlace(EntityPlaceEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = e.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
        if ( item == null) {
            return;
        }

        String type = item.getType().name();
        if (placeableEntities.stream().noneMatch((pre) -> pre.test(type))) {
            return;
        }

        Location loc = e.getBlock().getRelative(e.getBlockFace()).getLocation();
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMaths.toPosition(loc));
        if (regions.isEmpty()) {
            return;
        }

        RegionProtectionEntityPlaceEvent event = new RegionProtectionEntityPlaceEvent(e.getPlayer(), regions, e.getEntityType());
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }


}
