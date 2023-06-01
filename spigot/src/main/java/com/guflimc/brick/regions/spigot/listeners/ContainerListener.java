package com.guflimc.brick.regions.spigot.listeners;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.spigot.api.events.PlayerRegionsContainerOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collection;

public class ContainerListener implements Listener {

    private void containerOpen(Player player, Location loc, Inventory inventory, Cancellable e) {
        Collection<Region> regions = RegionAPI.get().regionsAt(SpigotMath.toBrickLocation(loc));
        if (regions.isEmpty()) {
            return;
        }

        PlayerRegionsContainerOpenEvent event = new PlayerRegionsContainerOpenEvent(player, regions, inventory);
        Bukkit.getPluginManager().callEvent(event);
        e.setCancelled(event.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {
            containerOpen(e.getPlayer(), e.getClickedBlock().getLocation(), e.getPlayer().getEnderChest(), e);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventory(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null || holder instanceof Player) {
            return;
        }

        InventoryType invtype = event.getInventory().getType();
        if (invtype == InventoryType.ANVIL || invtype == InventoryType.CRAFTING || invtype == InventoryType.CREATIVE
                || invtype == InventoryType.MERCHANT || invtype == InventoryType.PLAYER || invtype == InventoryType.WORKBENCH) {
            return;
        }

        Location loc;
        if (holder instanceof Entity) {
            loc = ((Entity) holder).getLocation();
        } else if (holder instanceof BlockState) {
            loc = ((BlockState) holder).getLocation();
        } else if (holder instanceof DoubleChest dc) {
            loc = new Location(dc.getWorld(), dc.getX(), dc.getY(), dc.getZ());
        } else {
            return;
        }

        containerOpen(player, loc, event.getInventory(), event);
    }

}