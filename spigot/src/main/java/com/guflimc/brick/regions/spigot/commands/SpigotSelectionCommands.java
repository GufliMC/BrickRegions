package com.guflimc.brick.regions.spigot.commands;

import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.selection.CubeSelection;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class SpigotSelectionCommands {

    private final SpigotBrickRegions plugin;

    public SpigotSelectionCommands(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

    @Command("br select wand")
    @Permission("brickregions.select")
    public void wand(@Source Player sender) {
        ItemStack item = ItemStackBuilder.of(Material.IRON_AXE)
                .withName(SpigotI18nAPI.get(this).string(sender, "select.wand.name"))
                .applyMeta(meta -> meta.getPersistentDataContainer()
                        .set(plugin.SELECTION_WAND_KEY, PersistentDataType.BYTE, (byte) 1))
                .build();

        sender.getInventory().addItem(item);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.wand");
    }

    @Command("br select type")
    @Permission("brickregions.select")
    public void mode(@Source Player sender, @Parameter SelectionType type) {
        sender.getPersistentDataContainer().set(plugin.SELECTION_TYPE, PersistentDataType.INTEGER, type.ordinal());
        SpigotI18nAPI.get(this).send(sender, "cmd.select.type", type.name());
    }

    @Command("br select border")
    @Permission("brickregions.select")
    public void worldborder(@Source Player sender) {
        WorldBorder wb = sender.getWorld().getWorldBorder();

        double min = sender.getWorld().getMinHeight();
        double max = sender.getWorld().getMaxHeight();

        CubeSelection sel = new CubeSelection(sender.getWorld().getUID());
        sel.setPos1(SpigotMath.toBrickVector(wb.getCenter().add(-wb.getSize() / 2, min, -wb.getSize() / 2)));
        sel.setPos2(SpigotMath.toBrickVector(wb.getCenter().add(wb.getSize() / 2, max, wb.getSize() / 2)));

        SpigotRegionAPI.get().setSelection(sender, sel);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.worldborder");
    }

    @Command("br select clear")
    @Permission("brickregions.select")
    public void clear(@Source Player sender) {
        SpigotRegionAPI.get().clearSelection(sender);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.clear");
    }

    @Command("br select undo")
    @Permission("brickregions.select")
    public void undo(@Source Player sender) {
        Optional<Selection> sel = SpigotRegionAPI.get().selection(sender);
        if ( sel.isEmpty() || !sel.get().isValid() ) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        sel.get().undo();
        SpigotI18nAPI.get(this).send(sender, "cmd.select.undo");
    }

    @Command("br select expand")
    @Permission("brickregions.select")
    public void expand(@Source Player sender) {
        Optional<Selection> sel = SpigotRegionAPI.get().selection(sender);
        if ( sel.isEmpty() || !sel.get().isValid() ) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        sel.get().expandY();
        SpigotI18nAPI.get(this).send(sender, "cmd.select.expand");
    }

}
