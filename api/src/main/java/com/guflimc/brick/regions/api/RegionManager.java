package com.guflimc.brick.regions.api;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
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

    Region.World world(@NotNull UUID worldId);

    Optional<Region.Keyed> region(@NotNull UUID id);

    Optional<Region.Keyed> region(@NotNull UUID worldId, @NotNull String name);

    Collection<Region> regions();

    <R extends Region> Collection<R> regions(@NotNull Class<R> type);

    <R extends Region, R1 extends Region> Collection<R> regions(@NotNull Class<R> type, @NotNull Class<R1> type1);

    <R extends Region, R1 extends Region, R2 extends Region> Collection<R> regions(@NotNull Class<R> type, @NotNull Class<R1> type1, @NotNull Class<R2> type2);

    Collection<Region> regions(@NotNull UUID worldId);

    <R extends Region> Collection<R> regions(@NotNull UUID worldId, @NotNull Class<R> type);

    <R extends Region, R1 extends Region> Collection<R> regions(@NotNull UUID worldId, @NotNull Class<R> type, @NotNull Class<R1> type1);

    <R extends Region, R1 extends Region, R2 extends Region> Collection<R> regions(@NotNull UUID worldId, @NotNull Class<R> type, @NotNull Class<R1> type1, @NotNull Class<R2> type2);

    Collection<Region> intersecting(@NotNull UUID worldId, @NotNull Shape3 shape);

    Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point3 position);

    Collection<Region> regionsAt(@NotNull Location position);

    CompletableFuture<Void> remove(@NotNull Region region);

    CompletableFuture<Void> persist(@NotNull Region region);

    void register(@NotNull Region region);

    void unregister(@NotNull Region region);

    //

    <T extends Region.Keyed & Region.Shaped> CompletableFuture<T> create(@NotNull String name, @NotNull UUID worldId, @NotNull Shape3 shape);

    <T extends Region.Keyed & Region.Shaped> CompletableFuture<T> create(@NotNull String name, @NotNull Selection selection);

    <T extends Region.Keyed & TileRegion> CompletableFuture<T> createHexagonTileRegion(@NotNull String name, @NotNull UUID worldId, int radius);

}
