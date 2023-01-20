package com.guflimc.brick.regions.api;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
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

    Collection<ShapeRegion> intersecting(Shape3 shape);

    Collection<ShapeRegion> intersecting(ShapeRegion region);

    Collection<Locality> localitiesAt(@NotNull UUID worldId, @NotNull Point3 position);

    Collection<Locality> localitiesAt(@NotNull Location position);

    Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point3 position);

    Collection<Region> regionsAt(@NotNull Location position);

    Collection<TileRegion> regionsTiledAt(@NotNull Location location);

    CompletableFuture<Void> remove(@NotNull Locality locality);

    CompletableFuture<Void> update(@NotNull Locality locality);

    void register(@NotNull Region region);

    void unregister(@NotNull Region region);

    //

    CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Shape3 shape);

    CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection);

}
