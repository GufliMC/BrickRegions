package com.guflimc.brick.regions.api;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector2;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.selection.Selection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RegionManager<S> {

    //

    void clearSelection(@NotNull S subject);

    Optional<Selection> selection(@NotNull S subject);

    void setSelection(@NotNull S subject, @NotNull Selection selection);

    //

    Optional<Region> findRegion(@NotNull UUID id);

    Optional<Region> findRegion(@NotNull String name);

    Collection<Region> regions();

    Collection<Region> regionsAt(@NotNull Point position);

    CompletableFuture<Void> remove(@NotNull Region region);

    CompletableFuture<Void> update(@NotNull Region region);

    //

    CompletableFuture<Region> create(@NotNull String name, @NotNull Area area);

    CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection);

    //

}
