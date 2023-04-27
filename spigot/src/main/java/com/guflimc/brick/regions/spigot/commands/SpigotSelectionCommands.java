package com.guflimc.brick.regions.spigot.commands;

import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

//@CommandContainer
public class SpigotSelectionCommands {

    private final SpigotBrickRegions plugin;

    public SpigotSelectionCommands(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

    @Command("br select wand")
    @Permission("brick.regions.select.wand")
    public void wand(@Source Player sender) {
        ItemStack item = ItemStackBuilder.of(Material.IRON_AXE)
                .withName(SpigotI18nAPI.get(this).string(sender, "select.wand.name"))
                .applyMeta(meta -> meta.getPersistentDataContainer()
                        .set(plugin.SELECTION_WAND_KEY, PersistentDataType.BYTE, (byte) 1))
                .build();

        sender.getInventory().addItem(item);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.wand");
    }

    @Command("br select")
    @Permission("brick.regions.select")
    public void type(@Source Player sender, @Parameter SelectionType type) {
        sender.getPersistentDataContainer().set(plugin.SELECTION_TYPE, PersistentDataType.INTEGER, type.ordinal());
        SpigotI18nAPI.get(this).send(sender, "cmd.select.type", type.name());
    }

    @Command("br select clear")
    @Permission("brick.regions.select.clear")
    public void clear(@Source Player sender) {
        SpigotRegionAPI.get().clearSelection(sender);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.clear");
    }

    @Command("br select undo")
    @Permission("brick.regions.select.undo")
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
    @Permission("brick.regions.select.expand")
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
