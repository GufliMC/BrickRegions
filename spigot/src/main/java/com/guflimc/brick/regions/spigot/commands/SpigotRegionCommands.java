package com.guflimc.brick.regions.spigot.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class SpigotRegionCommands {

    private final SpigotBrickRegions plugin;

    public SpigotRegionCommands(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

    @Command("br region list")
    @Permission("brickregions.region.list")
    public void list(@Source Player sender) {
        SpigotI18nAPI.get(this).send(sender, "cmd.region.list",
                RegionAPI.get().persistentRegions(sender.getWorld().getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @Command("br region list")
    @Permission("brickregions.region.list")
    public void listWorld(@Source Audience sender, @Parameter World world) {
        I18nAPI.get(this).send(sender, "cmd.region.list.world", world.getName(),
                RegionAPI.get().persistentRegions(world.getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @Command("br region create")
    @Permission("brickregions.region.create")
    public void create(@Source Player sender, @Parameter String name) {
        if (RegionAPI.get().region(sender.getWorld().getUID(), name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.region.create.error.exists", name);
            return;
        }

        Selection selection = SpigotRegionAPI.get().selection(sender).orElse(null);
        if (selection == null) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

//        Shape3 shape = selection.shape();
//        if (shape instanceof PolyPrism pa && pa.polygon().isComplex()) {
//            SpigotI18nAPI.get(this).send(sender, "cmd.regions.create.error.poly-invalid");
//            return;
//        }

        SpigotRegionAPI.get().create(name, selection);

        SpigotI18nAPI.get(this).send(sender, "cmd.region.create", name);
    }

    // TILE REGIONS

    @Command("br tileregion create")
    @Permission("brickregions.tileregion.create")
    public void createTiles(@Source Player sender,
                            @Parameter String name,
                            @Parameter int radius) {
        RegionAPI.get().createHexagonTileRegion(name, sender.getWorld().getUID(), radius).thenAccept((region) -> {
            SpigotI18nAPI.get(this).send(sender, "cmd.tileregion.create", name);
        });
    }

    @Command("br tileregion fill")
    @Permission("brickregions.tileregion.fill")
    public void fillTiles(@Source Player sender,
                            @Source Selection selection,
                            @Parameter TileRegion region) {
        Shape3 shape = selection.shape();
        if (shape instanceof PolyPrism pa && pa.polygon().isComplex()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.regions.create.error.poly-invalid");
            return;
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int count = region.groups().size();
            region.addGroups(shape.contour());
            RegionAPI.get().save(region);

            SpigotI18nAPI.get(this).send(sender, "cmd.tileregion.fill", region.name(), region.groups().size() - count);
        });
    }

    @Command("br tileregion groupify")
    @Permission("brickregions.tileregion.groupify")
    public void groupifyTiles(@Source Player sender,
                          @Parameter TileRegion region,
                          @Parameter int size) {
        region.merge(size);
        RegionAPI.get().save(region);

        SpigotI18nAPI.get(this).send(sender, "cmd.tileregion.groupify", region.name(), size);
    }
}
