package com.guflimc.brick.regions.spigot.squaremap;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Polygon;

import java.awt.*;
import java.util.List;
import java.util.*;

public class SquaremapRenderer {

    private final Squaremap squaremap;
    private final Map<MapWorld, SimpleLayerProvider> worlds = new HashMap<>();
    private final Map<TileRegion, SimpleLayerProvider> tileLayers = new HashMap<>();

    public SquaremapRenderer(Squaremap squaremap) {
        this.squaremap = squaremap;
    }

    private SimpleLayerProvider defaultLayer(@NotNull MapWorld world) {
        worlds.computeIfAbsent(world, (w) -> {
            SimpleLayerProvider layer = SimpleLayerProvider.builder("BrickRegions")
                    .showControls(true)
                    .defaultHidden(false)
                    .layerPriority(0)
                    .zIndex(200)
                    .build();
            world.layerRegistry().register(Key.of(layer.getLabel()), layer);
            layer.clearMarkers();
            return layer;
        });
        return worlds.get(world);
    }

    private Optional<MapWorld> mapWorld(@NotNull World world) {
        MapWorld mapWorld = squaremap.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).orElse(null);
        return Optional.ofNullable(mapWorld);
    }

    private Optional<MapWorld> mapWorld(@NotNull UUID worldId) {
        return Optional.ofNullable(Bukkit.getWorld(worldId)).flatMap(this::mapWorld);
    }

    public void clear(@NotNull World world) {
        MapWorld mapWorld = squaremap.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).orElse(null);
        if (mapWorld == null) {
            return;
        }

        if (!worlds.containsKey(mapWorld)) {
            return;
        }

        SimpleLayerProvider layer = worlds.remove(mapWorld);
        layer.clearMarkers();
        mapWorld.layerRegistry().unregister(Key.of(layer.getLabel()));
    }

    public void render() {
        worlds.keySet().forEach(this::render);
    }

    public void render(@NotNull World world) {
        mapWorld(world).ifPresent(this::render);
    }

    public void render(@NotNull Region region) {
        if (region instanceof TileGroup tg) {
            render(tg);
            return;
        }

        if (region instanceof Region.Shaped sr) {
            mapWorld(region.worldId()).ifPresent(mw -> render(mw, sr));
            return;
        }

        if (region instanceof TileRegion tr) {
            mapWorld(region.worldId()).ifPresent(mw -> render(mw, tr));
            return;
        }
    }

    private void render(@NotNull MapWorld mapWorld) {
        World world = BukkitAdapter.bukkitWorld(mapWorld);
        render(world, mapWorld);
    }

    private void render(@NotNull World world, @NotNull MapWorld mapWorld) {
        SimpleLayerProvider layer = defaultLayer(mapWorld);
        layer.clearMarkers();

        // render shape regions
        RegionAPI.get().regions(world.getUID()).stream()
                .filter(Region.Shaped.class::isInstance)
                .map(Region.Shaped.class::cast)
                .forEach(rg -> this.render(layer, rg));

        // render tiles
        RegionAPI.get().regions(world.getUID()).stream()
                .filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .filter(rg -> !rg.groups().isEmpty())
                .forEach(rg -> this.render(mapWorld, rg));
    }

    private void render(@NotNull MapWorld world, @NotNull Region.Shaped region) {
        SimpleLayerProvider layer = defaultLayer(world);
        render(layer, region);
    }

    private void render(@NotNull SimpleLayerProvider layer, @NotNull Region.Shaped region) {
        render(layer, region, region.shape().contour());
    }

    private void render(@NotNull MapWorld mapWorld, @NotNull TileRegion tileRegion) {
        tileLayers.computeIfAbsent(tileRegion, tr -> {
            SimpleLayerProvider layer = SimpleLayerProvider.builder("TR-" + tileRegion.hashCode())
                    .showControls(true)
                    .defaultHidden(false)
                    .layerPriority(1 + tileRegion.priority())
                    .zIndex(250 + tileRegion.priority())
                    .build();
            mapWorld.layerRegistry().register(Key.of(layer.getLabel()), layer);
            return layer;
        });

        // render tiles in layer
        SimpleLayerProvider layer = tileLayers.get(tileRegion);
        layer.clearMarkers();
        tileRegion.groups().forEach(group -> render(layer, group, group.shape()));
    }

    private void render(@NotNull TileGroup group) {
        SimpleLayerProvider layer = tileLayers.get(group.parent());
        layer.removeMarker(Key.of("RG-" + group.hashCode()));
        render(layer, group, group.shape());
    }

    private void render(@NotNull SimpleLayerProvider layer, @NotNull Region region, @NotNull Shape2 shape) {
        MarkerOptions options = MarkerOptions.defaultOptions().asBuilder()
                .fillRule(MarkerOptions.FillRule.NONZERO)
                .stroke(true)
                .strokeColor(Color.WHITE)
                .fill(true)
                .fillColor(Color.WHITE)
                .fillOpacity(0.2)
                .build();

        if (region instanceof Region.Attributeable.Attributeable al) {
            if (al.attribute(RegionAttributeKey.MAP_HIDDEN).orElse(false)) {
                return; // do not render
            }

            MarkerOptions.Builder builder = options.asBuilder();
            al.attribute(RegionAttributeKey.MAP_CLICK_TEXT).ifPresent(builder::clickTooltip);
            al.attribute(RegionAttributeKey.MAP_HOVER_TEXT).ifPresent(builder::hoverTooltip);
            al.attribute(RegionAttributeKey.MAP_FILL_COLOR).ifPresent(builder::fillColor);
            al.attribute(RegionAttributeKey.MAP_FILL_OPACITY).ifPresent(builder::fillOpacity);
            al.attribute(RegionAttributeKey.MAP_STROKE_COLOR).ifPresent(builder::strokeColor);
            al.attribute(RegionAttributeKey.MAP_STROKE_OPACITY).ifPresent(builder::strokeOpacity);
            al.attribute(RegionAttributeKey.MAP_STROKE_WEIGHT).ifPresent(builder::strokeWeight);
            options = builder.build();
        }

        // draw shape
        List<Point> vertices = new ArrayList<>();
        for (Point2 vec : shape.vertices()) {
            vertices.add(Point.of(vec.x(), vec.y()));
        }

        Polygon marker = Marker.polygon(vertices);
        marker.markerOptions(options);
        layer.addMarker(Key.of("RG-" + region.hashCode()), marker);
    }

    //

    public void delete(@NotNull Region region) {
        if (region instanceof TileGroup tg) {
            delete(tg);
            return;
        }

        if (region instanceof TileRegion tr) {
            mapWorld(region.worldId()).ifPresent(mw -> delete(mw, tr));
            return;
        }

        mapWorld(region.worldId()).ifPresent(mw -> delete(mw, region));
    }

    private void delete(@NotNull MapWorld world, @NotNull TileRegion region) {
        SimpleLayerProvider layer = tileLayers.get(region);
        if (layer == null) return;
        layer.clearMarkers();
        world.layerRegistry().unregister(Key.of(layer.getLabel()));
    }

    private void delete(@NotNull TileGroup group) {
        SimpleLayerProvider layer = tileLayers.get(group.parent());
        layer.removeMarker(Key.of("RG-" + group.hashCode()));
    }

    private void delete(@NotNull MapWorld world, @NotNull Region region) {
        SimpleLayerProvider layer = defaultLayer(world);
        layer.removeMarker(Key.of("RG-" + region.hashCode()));
    }

}
