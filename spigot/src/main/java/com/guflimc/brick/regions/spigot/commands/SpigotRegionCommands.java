package com.guflimc.brick.regions.spigot.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;
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

    @Command("br tiles fill")
    @Permission("brick.tiles.fill")
    public void fill(@Source Player player, @Source Tile tile, Material material) {
        Rectangle bounds = tile.shape().bounds();
        for ( int x = bounds.min().blockX(); x <= bounds.max().x(); x++ ) {
            for ( int z = bounds.min().blockY(); z <= bounds.max().y(); z++ ) {
                if ( tile.shape().contains(new Vector2(x, z)) )
                    player.getWorld().getBlockAt(x, player.getLocation().getBlockY(), z).setType(material);
            }
        }
    }

    @Command("br tiles contour")
    @Permission("brick.tiles.contour")
    public void contour(@Source Player player, @Source Tile tile, Material material) {
        List<Point2> points = tile.shape().vertices();
        Point2 from = points.get(points.size() - 1);
        for (Point2 to : points) {
            Vector dir = new Vector(to.x() - from.x(), 0, to.y() - from.y());
            Vector start = new Vector(from.x(), 0, from.y());
            BlockIterator iter = new BlockIterator(player.getWorld(), start, dir, player.getLocation().getY(), (int) Math.ceil(dir.length()));
            while (iter.hasNext()) {
                iter.next().setType(material);
            }
            from = to;
        }

    }

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

//        Shape3 shape = selection.shape();
//        if (shape instanceof PolyPrism pa && pa.polygon().isComplex()) {
//            SpigotI18nAPI.get(this).send(sender, "cmd.regions.create.error.poly-invalid");
//            return;
//        }

        SpigotRegionAPI.get().create(name, selection);

        SpigotI18nAPI.get(this).send(sender, "cmd.regions.create", name);
    }

    @Command("br tiles generate")
    @Permission("brick.tiles.generate")
    public void createTiles(@Source Player sender,
                            @Parameter String name,
                            @Parameter int radius) {
        Selection selection = SpigotRegionAPI.get().selection(sender).orElse(null);
        if (selection == null) {
            SpigotI18nAPI.get(this).send(sender, "cmd.select.invalid");
            return;
        }

        Shape3 shape = selection.shape();
        if (shape instanceof PolyPrism pa && pa.polygon().isComplex()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.regions.create.error.poly-invalid");
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
