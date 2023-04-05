package com.guflimc.brick.regions.squaremap;

import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.*;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Polygon;

import java.awt.*;
import java.util.*;
import java.util.List;

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

        if ( worlds.containsKey(mapWorld) ) {
            worlds.get(mapWorld).clearMarkers();
            worlds.remove(mapWorld);
        }
    }

    public void render() {
        worlds.keySet().forEach(this::render);
    }

    public void render(@NotNull World world) {
        mapWorld(world).ifPresent(this::render);
    }

    public void render(@NotNull Locality locality) {
        if ( locality instanceof Tile tile ) {
            render(tile);
            return;
        }

        if ( locality instanceof ShapeRegion sr ) {
            mapWorld(locality.worldId()).ifPresent(mw -> render(mw, sr));
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
                .filter(ShapeRegion.class::isInstance)
                .map(ShapeRegion.class::cast)
                .forEach(rg -> this.render(layer, rg));

        // render tiles
        RegionAPI.get().regions(world.getUID()).stream()
                .filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .filter(rg -> !rg.tiles().isEmpty())
                .forEach(rg -> this.render(mapWorld, rg));
    }

    private void render(@NotNull MapWorld world, @NotNull ShapeRegion region) {
        SimpleLayerProvider layer = defaultLayer(world);
        render(layer, region);
    }

    private void render(@NotNull SimpleLayerProvider layer, @NotNull ShapeRegion region) {
        render(layer, region, region.shape().contour());
    }

    private void render(@NotNull MapWorld mapWorld, @NotNull TileRegion tileRegion) {
        tileLayers.computeIfAbsent(tileRegion, tr -> {
            SimpleLayerProvider layer = SimpleLayerProvider.builder("Tiles-" + tileRegion.name())
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
        tileRegion.tiles().forEach(tile -> render(layer, tile, tile.shape()));
    }

    private void render(@NotNull Tile tile) {
        SimpleLayerProvider layer = tileLayers.get(tile.parent());
        layer.removeMarker(Key.of(tile.id().toString()));
        render(layer, tile, tile.shape());
    }

    private void render(@NotNull SimpleLayerProvider layer, @NotNull Locality locality, @NotNull Shape2 shape) {
        MarkerOptions options = MarkerOptions.defaultOptions().asBuilder()
                .fillRule(MarkerOptions.FillRule.NONZERO)
                .stroke(true)
                .strokeColor(Color.WHITE)
                .fill(true)
                .fillColor(Color.WHITE)
                .fillOpacity(0.2)
                .build();

        if ( locality instanceof AttributedLocality al ) {
            if ( al.attribute(LocalityAttributeKey.MAP_HIDDEN).orElse(false) ) {
                return; // do not render
            }

            MarkerOptions.Builder builder = options.asBuilder();
            if ( locality instanceof Tile ) {
                builder.strokeWeight(1); // default for tiles
            }

            al.attribute(LocalityAttributeKey.MAP_CLICK_TEXT).ifPresent(builder::clickTooltip);
            al.attribute(LocalityAttributeKey.MAP_HOVER_TEXT).ifPresent(builder::hoverTooltip);
            al.attribute(LocalityAttributeKey.MAP_FILL_COLOR).ifPresent(builder::fillColor);
            al.attribute(LocalityAttributeKey.MAP_FILL_OPACITY).ifPresent(builder::fillOpacity);
            al.attribute(LocalityAttributeKey.MAP_STROKE_COLOR).ifPresent(builder::strokeColor);
            al.attribute(LocalityAttributeKey.MAP_STROKE_OPACITY).ifPresent(builder::strokeOpacity);
            al.attribute(LocalityAttributeKey.MAP_STROKE_WEIGHT).ifPresent(builder::strokeWeight);
            options = builder.build();
        }

        // draw shape
        List<Point> vertices = new ArrayList<>();
        for (Vector2 vec : shape.vertices()) {
            vertices.add(Point.of(vec.x(), vec.y()));
        }

        Polygon marker = Marker.polygon(vertices);
        marker.markerOptions(options);
        layer.addMarker(Key.of(locality.id().toString()), marker);
    }
}