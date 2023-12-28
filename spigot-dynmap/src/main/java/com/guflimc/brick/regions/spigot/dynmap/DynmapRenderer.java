package com.guflimc.brick.regions.spigot.dynmap;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.tile.TileKey;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;
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
                .filter(Region.Shaped.class::isInstance)
                .map(Region.Shaped.class::cast)
                .forEach(rg -> this.render(world, rg));

        // render tile groups
        RegionAPI.get().regions(world.getUID()).stream()
                .filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .filter(rg -> !rg.groups().isEmpty())
                .forEach(rg -> this.render(world, rg));
    }

    public void render(@NotNull Region region) {
        if (region instanceof TileGroup tg) {
            render(tg);
            return;
        }

        if (region instanceof Region.Shaped sr) {
            world(sr.worldId()).ifPresent(w -> render(w, sr));
            return;
        }

        if (region instanceof TileRegion tr) {
            world(tr.worldId()).ifPresent(w -> render(w, tr));
            return;
        }
    }

    private void render(@NotNull World world, @NotNull Region.Shaped region) {
        delete(region);
        render(this.markerSet, world, region, region.shape().contour());
    }

    private void render(@NotNull World world, @NotNull TileRegion tileRegion) {
        MarkerSet ms = tileLayers.remove(tileRegion);
        if (ms != null) {
            ms.getMarkers().forEach(GenericMarker::deleteMarker);
            ms.deleteMarkerSet();
        }

        ms = dynmap.getMarkerAPI().createMarkerSet("TR-" + tileRegion.hashCode(), "", null, false);
        ms.setHideByDefault(false);
        ms.setLayerPriority(1 + tileRegion.priority());
        tileLayers.put(tileRegion, ms);

        // render tiles in layer
        MarkerSet markerSet = ms;
        tileRegion.groups().forEach(group -> render(markerSet, world, group, group.shape()));
    }

    private void render(@NotNull TileGroup group) {
        World world = world(group.worldId()).orElseThrow();
        MarkerSet ms = tileLayers.get(group.parent());
        if (ms == null) {
            render(group.parent());
            return;
        }

        ms.getAreaMarkers().stream()
                .filter(m -> m.getMarkerID().equals("RG-" + group.hashCode()))
                .findFirst().ifPresent(GenericMarker::deleteMarker);
        render(ms, world, group, group.shape());
    }

    private void render(@NotNull MarkerSet markerSet, @NotNull World world, @NotNull Region region, @NotNull Shape2 shape) {
        double[] xPoints = shape.vertices().stream().mapToDouble(Point2::x).toArray();
        double[] zPoints = shape.vertices().stream().mapToDouble(Point2::y).toArray();

        String label = "";
        if (region instanceof TileGroup tg && tg.tiles().size() == 1) {
            TileKey t = tg.tiles().iterator().next();
            label = t.x() + ", " + t.z();
        } else if (region instanceof Region.Named rk) {
            label = rk.name();
        }

        AreaMarker am = markerSet.createAreaMarker("RG-" + region.hashCode(), label, true, world.getName(), xPoints, zPoints, false);

        int fillColor = Color.WHITE.getRGB();
        double fillOpacity = 0.25;

        int strokeColor = Color.WHITE.getRGB();
        double strokeOpacity = 0.8;
        int strokeWeight = 2;

        if ( region instanceof Region.Attributeable al) {
            if (al.attribute(RegionAttributeKey.MAP_HIDDEN).orElse(false)) {
                am.deleteMarker();
                return; // do not render
            }

            al.attribute(RegionAttributeKey.MAP_CLICK_TEXT).ifPresent(am::setDescription);
            al.attribute(RegionAttributeKey.MAP_HOVER_TEXT).ifPresent(am::setDescription);

            fillColor = al.attribute(RegionAttributeKey.MAP_FILL_COLOR).map(c -> c.getRGB() & 0xFFFFFF).orElse(fillColor);
            fillOpacity = al.attribute(RegionAttributeKey.MAP_FILL_OPACITY).orElse(fillOpacity);
            strokeColor = al.attribute(RegionAttributeKey.MAP_STROKE_COLOR).map(c -> c.getRGB() & 0xFFFFFF).orElse(strokeColor);
            strokeOpacity = al.attribute(RegionAttributeKey.MAP_STROKE_OPACITY).orElse(strokeOpacity);
            strokeWeight = al.attribute(RegionAttributeKey.MAP_STROKE_WEIGHT).orElse(strokeWeight);
        }

        am.setFillStyle(fillOpacity, fillColor);
        am.setLineStyle(strokeWeight, strokeOpacity, strokeColor);
    }

    //

    public void delete(@NotNull Region region) {
        if (region instanceof TileRegion tr) {
            delete(tr);
            return;
        }

        markerSet.getAreaMarkers().stream()
                .filter(m -> m.getMarkerID().equals("RG-" + region.hashCode()))
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
