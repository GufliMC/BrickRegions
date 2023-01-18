package com.guflimc.brick.regions.squaremap;

import com.google.gson.Gson;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.TiledRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Polygon;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BrickRegionsSquaremap extends JavaPlugin implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(BrickRegionsSquaremap.class);

    public final Gson gson = new Gson();

    //

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        for (World world : Bukkit.getWorlds()) {
            load(world);
        }

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getDescription().getName() + " v" + getDescription().getVersion();
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        load(event.getWorld());
    }

    private void load(World world) {
        logger.info("Rendering world " + world.getName() + ".");

        Squaremap api = SquaremapProvider.get();
        MapWorld mapWorld = api.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).orElse(null);
        if ( mapWorld == null ) return;

        RegionAPI.get().regions(world.getUID()).stream()
                .filter(TiledRegion.class::isInstance)
                .map(TiledRegion.class::cast)
                .filter(rg -> !rg.tiles().isEmpty())
                .forEach(rg -> this.render(rg, mapWorld));
    }

    private void render(TiledRegion region, MapWorld world) {
        logger.info("Rendering region " + region.name() + ".");

        SimpleLayerProvider layer = SimpleLayerProvider.builder(region.name())
                .showControls(true)
                .defaultHidden(false)
                .layerPriority(region.priority())
                .zIndex(250 + region.priority())
                .build();

        world.layerRegistry().register(Key.of(region.name()), layer);

        for (Tile tile : region.tiles() ) {
            List<Point> vertices = new ArrayList<>();
            for (Vector2 vec : tile.polygon().vertices() ) {
                vertices.add(Point.of(vec.x(), vec.y()));
            }

            Polygon marker = Marker.polygon(vertices);
            MarkerOptions options = marker.markerOptions().asBuilder()
                    .stroke(true)
                    .strokeColor(Color.CYAN)
                    .hoverTooltip("Tile: " + (int) tile.position().x() + "; " + (int) tile.position().y())
                    .build();
            marker.markerOptions(options);

            layer.addMarker(Key.of("tile-" + tile.position().x() + "-" + tile.position().y()), marker);
        }
    }

}
