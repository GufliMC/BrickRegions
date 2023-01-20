package com.guflimc.brick.regions.spigot.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.i18n.spigot.api.SpigotI18nAPI;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.spigot.SpigotBrickRegions;
import com.guflimc.brick.regions.spigot.api.SpigotRegionAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

//@CommandContainer
public class SpigotRegionCommands {

    private final SpigotBrickRegions plugin;

    public SpigotRegionCommands(SpigotBrickRegions plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("br benchmark start")
    @CommandPermission("brick.regions.benchmark")
    public void benchmarkStart(Audience audience) {
        audience.sendMessage(Component.text("Benchmark started."));
        plugin.benchmark.start();
    }

    @CommandMethod("br benchmark stop")
    @CommandPermission("brick.regions.benchmark.stop")
    public void benchmarkStop(Audience audience) {
        audience.sendMessage(Component.text("Benchmark stopped. Check console for results."));
        plugin.benchmark.stop();
    }

    @CommandMethod("br list")
    @CommandPermission("brick.regions.list")
    public void list(Player sender) {
        SpigotI18nAPI.get(this).send(sender, "cmd.region.list",
                RegionAPI.get().persistentRegions(sender.getWorld().getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @CommandMethod("br list <world>")
    @CommandPermission("brick.regions.list")
    public void listWorld(Audience sender, @Argument(value = "world") World world) {
        I18nAPI.get(this).send(sender, "cmd.region.list.world", world.getName(),
                RegionAPI.get().persistentRegions(world.getUID()).stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @CommandMethod("br create <name>")
    @CommandPermission("brick.regions.create")
    public void create(Player sender, @Argument(value = "name") String name) {
        if (RegionAPI.get().findRegion(sender.getWorld().getUID(), name).isPresent()) {
            SpigotI18nAPI.get(this).send(sender, "cmd.region.create.error.exists", name);
            return;
        }

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

        SpigotRegionAPI.get().create(name, selection);
        SpigotRegionAPI.get().clearSelection(sender);

        SpigotI18nAPI.get(this).send(sender, "cmd.region.create", name);
    }

}
