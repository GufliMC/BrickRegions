package com.guflimc.brick.regions.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.PolyArea;
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
            SpigotI18nAPI.get(this).send(sender, "cmd.create.error.exists", name);
            return;
        }

        Selection selection = SpigotRegionAPI.get().selection(sender).orElse(null);
        if ( selection == null ) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }


        Area area = selection.area();
        if ( area instanceof PolyArea pa && !pa.isConvex() ) {
            SpigotI18nAPI.get(this).send(sender, "cmd.create.poly-invalid");
            return;
        }

        SpigotRegionAPI.get().create(name, selection);

        SpigotI18nAPI.get(this).send(sender, "cmd.region.create", name);
    }

}
