package com.guflimc.brick.regions.api;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.*;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableRegion;
import com.guflimc.brick.regions.api.domain.WorldRegion;
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

    Optional<Region> findRegion(@NotNull UUID id);

    Optional<Region> findRegion(@NotNull UUID worldId, @NotNull String name);

    Collection<Region> regions();

    Collection<Region> regions(@NotNull UUID worldId);

    Collection<ModifiableRegion> persistentRegions();

    Collection<ModifiableRegion> persistentRegions(@NotNull UUID worldId);

    WorldRegion worldRegion(@NotNull UUID worldId);

    Collection<ShapeRegion> intersecting(@NotNull UUID worldId, @NotNull Shape3 shape);

    Collection<ShapeRegion> intersecting(@NotNull ShapeRegion region);

    Collection<Tile> intersecting(@NotNull UUID worldId, @NotNull Shape2 shape);

    Collection<Locality> localitiesAt(@NotNull UUID worldId, @NotNull Point3 position);

    Collection<Locality> localitiesAt(@NotNull Location position);

    Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point3 position);

    Collection<Region> regionsAt(@NotNull Location position);

    Optional<TileRegion> tileRegionAt(@NotNull Location location);

    Optional<Tile> tileAt(@NotNull Location location);

    CompletableFuture<Void> delete(@NotNull Locality locality);

    CompletableFuture<Void> save(@NotNull Locality locality);

    <T extends Locality> CompletableFuture<Void> save(@NotNull Collection<T> localities);

    void register(@NotNull Region region);

    void unregister(@NotNull Region region);

    //

    CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Shape3 shape);

    CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection);

    CompletableFuture<TileRegion> createTiles(@NotNull String name, int tileRadius, @NotNull UUID worldId, @NotNull Shape3 shape);

    CompletableFuture<TileRegion> createTiles(@NotNull String name, int tileRadius, @NotNull Selection selection);
}
