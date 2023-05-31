package com.guflimc.brick.regions.api;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.locality.Locality;
import com.guflimc.brick.regions.api.domain.region.Region;
import com.guflimc.brick.regions.api.domain.region.RegionKey;
import com.guflimc.brick.regions.api.domain.region.ShapeRegion;
import com.guflimc.brick.regions.api.domain.region.WorldRegion;
import com.guflimc.brick.regions.api.domain.region.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.region.tile.TileKey;
import com.guflimc.brick.regions.api.domain.region.tile.TileRegion;
import com.guflimc.brick.regions.api.selection.Selection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RegionManager<S> {

    // SELECTION

    void clearSelection(@NotNull S subject);

    Optional<Selection> selection(@NotNull S subject);

    void setSelection(@NotNull S subject, @NotNull Selection selection);

    // REGIONS

    Optional<Region> region(@NotNull UUID id);

    Optional<Region> region(@NotNull UUID worldId, @NotNull RegionKey key);

    Collection<Region> regions();

    Collection<Region> regions(@NotNull UUID worldId);

    Collection<Region> regions(@NotNull UUID worldId, @NotNull String namespace);

    WorldRegion worldRegion(@NotNull UUID worldId);

    Collection<ShapeRegion> intersecting(@NotNull UUID worldId, @NotNull Shape3 shape);

    Collection<ShapeRegion> intersecting(@NotNull ShapeRegion region);

    Collection<TileGroup> intersecting(@NotNull UUID worldId, @NotNull Shape2 shape);

    Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point3 position);

    Collection<Region> regionsAt(@NotNull Location position);

//    Optional<TileRegion> tileRegionAt(@NotNull Location location);
//
//    Optional<TileGroup> tileGroupAt(@NotNull Location location);

    CompletableFuture<Void> delete(@NotNull Locality locality);

    CompletableFuture<Void> save(@NotNull Locality locality);

    <T extends Locality> CompletableFuture<Void> save(@NotNull Collection<T> localities);

    void register(@NotNull Region region);

    void unregister(@NotNull Region region);

    //

    CompletableFuture<Region> create(@NotNull RegionKey key, @NotNull UUID worldId, @NotNull Shape3 shape);

    CompletableFuture<Region> create(@NotNull RegionKey key, @NotNull Selection selection);

    CompletableFuture<TileRegion> createHexagonTileRegion(@NotNull RegionKey key, @NotNull UUID worldId, int radius);

}
