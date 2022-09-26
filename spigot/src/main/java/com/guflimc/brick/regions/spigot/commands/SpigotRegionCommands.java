package com.guflimc.brick.regions.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import org.bukkit.entity.Player;

import java.util.Optional;

//@CommandContainer
public class SpigotRegionCommands {

    @CommandMethod("br create <name>")
    public void create(Player sender, @Argument(value = "name") String name) {
        if (RegionAPI.get().findRegion(name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.create.exists", name);
            return;
        }

        Optional<Selection> selection = SpigotRegionAPI.get().selection(sender);
        if (selection.isEmpty()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        SpigotRegionAPI.get().create(name, selection.get());

        SpigotI18nAPI.get(this).send(sender, "cmd.region.create", name);
    }

}
