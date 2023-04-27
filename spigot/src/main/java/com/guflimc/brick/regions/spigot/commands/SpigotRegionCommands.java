package com.guflimc.brick.regions.spigot.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

//@CommandContainer
public class SpigotRegionCommands {

    private final SpigotBrickRegions plugin;

    public SpigotRegionCommands(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

//    @Command("br benchmark start")
//    @Permission("brick.regions.benchmark")
//    public void benchmarkStart(Audience audience) {
//        audience.sendMessage(Component.text("Benchmark started."));
//        plugin.benchmark.start();
//    }
//
//    @Command("br benchmark stop")
//    @Permission("brick.regions.benchmark.stop")
//    public void benchmarkStop(Audience audience) {
//        audience.sendMessage(Component.text("Benchmark stopped. Check console for results."));
//        plugin.benchmark.stop();
//    }

    @Command("br regions list")
    @Permission("brick.regions.list")
    public void list(@Source Player sender) {
        SpigotI18nAPI.get(this).send(sender, "cmd.regions.list",
                RegionAPI.get().persistentRegions(sender.getWorld().getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @Command("br regions list")
    @Permission("brick.regions.list")
    public void listWorld(@Source Audience sender, @Parameter World world) {
        I18nAPI.get(this).send(sender, "cmd.regions.list.world", world.getName(),
                RegionAPI.get().persistentRegions(world.getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @Command("br regions create")
    @Permission("brick.regions.create")
    public void create(@Source Player sender, @Parameter String name) {
        if (RegionAPI.get().findRegion(sender.getWorld().getUID(), name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.regions.create.error.exists", name);
            return;
        }

        Selection selection = SpigotRegionAPI.get().selection(sender).orElse(null);
        if (selection == null) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        Shape3 shape = selection.shape();
        if (shape instanceof PolyPrism pa && !pa.polygon().isConvex()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.regions.create.error.poly-invalid");
            return;
        }

        SpigotRegionAPI.get().create(name, selection);
        SpigotRegionAPI.get().clearSelection(sender);

        SpigotI18nAPI.get(this).send(sender, "cmd.regions.create", name);
    }

    @Command("br tiles create")
    @Permission("brick.tiles.create")
    public void createTiles(@Source Player sender,
                            @Parameter String name,
                            @Parameter int radius) {
        Selection selection = SpigotRegionAPI.get().selection(sender).orElse(null);
        if (selection == null) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        Shape3 shape = selection.shape();
        if (shape instanceof PolyPrism pa && !pa.polygon().isConvex()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.region.create.error.poly-invalid");
            return;
        }

        try {
            RegionAPI.get().createTiles(name, radius, selection).thenAccept((region) -> {
                SpigotI18nAPI.get(this).send(sender, "cmd.tiles.create", name, region.tiles().size());
            });
        } catch (Exception ex) {
            SpigotI18nAPI.get(this).send(sender, "cmd.tiles.create.error.intersect");
        }
    }
}
