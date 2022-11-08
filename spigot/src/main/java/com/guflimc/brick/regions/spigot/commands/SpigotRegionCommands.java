package com.guflimc.brick.regions.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.PolyArea;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import net.kyori.adventure.audience.Audience;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

//@CommandContainer
public class SpigotRegionCommands {

    @CommandMethod("br list")
    public void list(Player sender) {
        SpigotI18nAPI.get(this).send(sender, "cmd.region.list",
                RegionAPI.get().persistentRegions(sender.getWorld().getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @CommandMethod("br list <world>")
    public void listWorld(Audience sender, World world) {
        I18nAPI.get(this).send(sender, "cmd.region.list",
                RegionAPI.get().persistentRegions(world.getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @CommandMethod("br create <name>")
    public void create(Player sender, @Argument(value = "name") String name) {
        if (RegionAPI.get().findRegion(sender.getWorld().getUID(), name).isPresent()) {
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
