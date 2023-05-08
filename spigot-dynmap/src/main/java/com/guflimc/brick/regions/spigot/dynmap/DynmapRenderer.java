package com.guflimc.brick.regions.spigot.dynmap;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class DynmapRenderer {

    private final DynmapAPI dynmap;

    private final MarkerSet markerSet;
    private final Map<TileRegion, MarkerSet> tileLayers = new HashMap<>();

    public DynmapRenderer(DynmapAPI dynmap) {
        this.dynmap = dynmap;
        MarkerSet markerSet = dynmap.getMarkerAPI().getMarkerSet("BrickRegions");
        if (markerSet == null) {
            markerSet = dynmap.getMarkerAPI().createMarkerSet("BrickRegions", "BrickRegions", null, false);
            markerSet.setHideByDefault(false);
            markerSet.setLayerPriority(1);
        }
        this.markerSet = markerSet;
    }

    private Optional<World> world(@NotNull UUID id) {
        return Optional.ofNullable(Bukkit.getWorld(id));
    }

    public void clear(@NotNull World world) {
        Set<AreaMarker> markers = markerSet.getAreaMarkers().stream()
                .filter(m -> m.getWorld().equals(world.getName()))
                .collect(Collectors.toSet());
        markers.forEach(GenericMarker::deleteMarker);
    }

    public void render() {
        Bukkit.getWorlds().forEach(this::render);
    }

    public void render(@NotNull World world) {
        clear(world);

        // render shape regions
        RegionAPI.get().regions(world.getUID()).stream()
                .filter(ShapeRegion.class::isInstance)
                .map(ShapeRegion.class::cast)
                .forEach(rg -> this.render(world, rg));

        // render tiles
        RegionAPI.get().regions(world.getUID()).stream()
                .filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .filter(rg -> !rg.tiles().isEmpty())
                .forEach(rg -> this.render(world, rg));
    }

    public void render(@NotNull Locality locality) {
        if (locality instanceof Tile tile) {
            render(tile);
            return;
        }

        if (locality instanceof ShapeRegion sr) {
            world(sr.worldId()).ifPresent(w -> render(w, sr));
            return;
        }

        if (locality instanceof TileRegion tr) {
            world(tr.worldId()).ifPresent(w -> render(w, tr));
            return;
        }
    }

    private void render(@NotNull World world, @NotNull ShapeRegion region) {
        render(this.markerSet, world, region, region.shape().contour());
    }

    private void render(@NotNull World world, @NotNull TileRegion tileRegion) {
        MarkerSet ms = tileLayers.remove(tileRegion);
        if ( ms != null ) {
            ms.deleteMarkerSet();
        }

        ms = dynmap.getMarkerAPI().createMarkerSet("Tiles-" + tileRegion.name(), "Tiles-" + tileRegion.name(), null, false);
        ms.setHideByDefault(false);
        ms.setLayerPriority(1 + tileRegion.priority());
        tileLayers.put(tileRegion, ms);

        // render tiles in layer
        MarkerSet markerSet = ms;
        tileRegion.tiles().forEach(tile -> render(markerSet, world, tile, tile.shape()));
    }

    private void render(@NotNull Tile tile) {
        World world = world(tile.worldId()).orElseThrow();
        MarkerSet ms = tileLayers.get(tile.parent());
        ms.getAreaMarkers().stream()
                .filter(m -> m.getMarkerID().equals(tile.id().toString()))
                .findFirst().ifPresent(GenericMarker::deleteMarker);
        render(ms, world, tile, tile.shape());
    }

    private void render(@NotNull MarkerSet markerSet, @NotNull World world, @NotNull Locality locality, @NotNull Shape2 shape) {
        double[] xPoints = shape.vertices().stream().mapToDouble(Point2::x).toArray();
        double[] zPoints = shape.vertices().stream().mapToDouble(Point2::y).toArray();

        String label = "";
        if ( locality instanceof Tile t) {
            label = t.position().blockX() + ", " + t.position().blockY();
        } else if ( locality instanceof Region r) {
            label = r.name();
        }

        AreaMarker am = markerSet.createAreaMarker(locality.id().toString(), label, true, world.getName(), xPoints, zPoints, false);

        int fillColor = Color.WHITE.getRGB();
        double fillOpacity = 0.25;

        int strokeColor = Color.WHITE.getRGB();
        double strokeOpacity = 0.8;
        int strokeWeight = 2;

        if (locality instanceof AttributedLocality al) {
            if (al.attribute(LocalityAttributeKey.MAP_HIDDEN).orElse(false)) {
                am.deleteMarker();
                return; // do not render
            }

            al.attribute(LocalityAttributeKey.MAP_CLICK_TEXT).ifPresent(am::setDescription);
            al.attribute(LocalityAttributeKey.MAP_HOVER_TEXT).ifPresent(am::setDescription);

            fillColor = al.attribute(LocalityAttributeKey.MAP_FILL_COLOR).map(c -> c.getRGB() & 0xFFFFFF).orElse(fillColor);
            fillOpacity = al.attribute(LocalityAttributeKey.MAP_FILL_OPACITY).orElse(fillOpacity);
            strokeColor = al.attribute(LocalityAttributeKey.MAP_STROKE_COLOR).map(c -> c.getRGB() & 0xFFFFFF).orElse(strokeColor);
            strokeOpacity = al.attribute(LocalityAttributeKey.MAP_STROKE_OPACITY).orElse(strokeOpacity);
            strokeWeight = al.attribute(LocalityAttributeKey.MAP_STROKE_WEIGHT).orElse(strokeWeight);
        }

        am.setFillStyle(fillOpacity, fillColor);
        am.setLineStyle(strokeWeight, strokeOpacity, strokeColor);
    }

    //

    public void delete(@NotNull Locality locality) {
        if (locality instanceof TileRegion tr) {
            delete(tr);
            return;
        }

        markerSet.getAreaMarkers().stream()
                .filter(m -> m.getMarkerID().equals(locality.id().toString()))
                .findFirst()
                .ifPresent(GenericMarker::deleteMarker);
    }

    private void delete(@NotNull TileRegion region) {
        MarkerSet set = tileLayers.remove(region);
        if (set != null) {
            set.deleteMarkerSet();
        }
    }

}
