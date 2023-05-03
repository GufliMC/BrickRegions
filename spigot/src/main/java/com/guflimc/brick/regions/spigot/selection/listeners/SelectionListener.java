package com.guflimc.brick.regions.spigot.selection.listeners;

import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.math.common.geometry.pos3.Vector3;
import com.guflimc.brick.regions.api.selection.CubeSelection;
import com.guflimc.brick.regions.api.selection.PolySelection;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class SelectionListener implements Listener {

    private final SpigotBrickRegions plugin;

    public SelectionListener(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ( event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }

        if ( item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer()
                .has(plugin.SELECTION_WAND_KEY, PersistentDataType.BYTE)) {
            return;
        }

        Block b = event.getClickedBlock();
        if ( b == null ) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            addLeftClick(event.getPlayer(), new Vector3(b.getX(), b.getY(), b.getZ()));
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            addRightClick(event.getPlayer(), new Vector3(b.getX(), b.getY(), b.getZ()));
            event.setCancelled(true);
        }
    }

    //

    private void addLeftClick(Player player, Vector3 pos) {
        Selection selection = getOrCreateSelection(player);
        if (selection instanceof PolySelection ps) {
            ps.add(pos);
            SpigotI18nAPI.get(this).send(player, "select.poly");
        } else if (selection instanceof CubeSelection ss) {
            ss.setPos1(pos);
            SpigotI18nAPI.get(this).send(player, "select.cuboid.primary");
        }
    }

    //

    private void addRightClick(Player player, Vector3 pos) {
        Selection selection = getOrCreateSelection(player);
        if (selection instanceof PolySelection ps) {
            ps.add(pos);
            SpigotI18nAPI.get(this).send(player, "select.poly");
        } else if (selection instanceof CubeSelection ss) {
            ss.setPos2(pos);
            SpigotI18nAPI.get(this).send(player, "select.cuboid.secondary");
        }
    }

    private Selection getOrCreateSelection(Player player) {
        Selection selection = SpigotRegionAPI.get().selection(player).orElse(null);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (selection == null) {
            if (pdc.has(plugin.SELECTION_TYPE, PersistentDataType.INTEGER)
                    && Objects.equals(pdc.get(plugin.SELECTION_TYPE, PersistentDataType.INTEGER), SelectionType.POLY.ordinal())) {
                selection = new PolySelection(player.getWorld().getUID());
            } else {
                selection = new CubeSelection(player.getWorld().getUID());
            }
        } else if (pdc.has(plugin.SELECTION_TYPE, PersistentDataType.INTEGER)
                && Objects.equals(pdc.get(plugin.SELECTION_TYPE, PersistentDataType.INTEGER), SelectionType.POLY.ordinal())) {
            if (selection instanceof CubeSelection) {
                selection = new PolySelection(player.getWorld().getUID());
            }
        } else {
            if (selection instanceof PolySelection) {
                selection = new CubeSelection(player.getWorld().getUID());
            }
        }
        SpigotRegionAPI.get().setSelection(player, selection);
        return selection;
    }

}
