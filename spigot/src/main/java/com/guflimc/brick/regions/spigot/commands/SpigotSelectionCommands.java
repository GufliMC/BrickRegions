package com.guflimc.brick.regions.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.gui.spigot.item.ItemStackBuilder;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
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

    @CommandMethod("br select wand")
    public void wand(Player sender) {
        ItemStack item = ItemStackBuilder.of(Material.IRON_AXE)
                .withName(SpigotI18nAPI.get(this).string(sender, "select.wand.name"))
                .applyMeta(meta -> meta.getPersistentDataContainer()
                        .set(plugin.SELECTION_WAND_KEY, PersistentDataType.BYTE, (byte) 1))
                .build();

        sender.getInventory().addItem(item);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.wand");
    }

    @CommandMethod("br select <type>")
    public void type(Player sender, @Argument(value = "type") SelectionType type) {
        sender.getPersistentDataContainer().set(plugin.SELECTION_TYPE, PersistentDataType.INTEGER, type.ordinal());
        SpigotI18nAPI.get(this).send(sender, "cmd.select.type", type.name());
    }

    @CommandMethod("br select clear")
    public void clear(Player sender) {
        SpigotRegionAPI.get().clearSelection(sender);
        SpigotI18nAPI.get(this).send(sender, "cmd.select.clear");
    }

    @CommandMethod("br select undo")
    public void undo(Player sender) {
        Optional<Selection> sel = SpigotRegionAPI.get().selection(sender);
        if ( sel.isEmpty() || !sel.get().isValid() ) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        sel.get().undo();
        SpigotI18nAPI.get(this).send(sender, "cmd.select.undo");
    }

    @CommandMethod("br select expand")
    public void expand(Player sender) {
        Optional<Selection> sel = SpigotRegionAPI.get().selection(sender);
        if ( sel.isEmpty() || !sel.get().isValid() ) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        sel.get().expandY();
        SpigotI18nAPI.get(this).send(sender, "cmd.select.expand");
    }

}
