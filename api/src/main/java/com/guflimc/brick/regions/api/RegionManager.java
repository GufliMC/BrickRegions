package com.guflimc.brick.regions.api;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.selection.Selection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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

    Optional<Region> findRegion(@NotNull UUID worldId, @NotNull String name);

    Collection<Region> regions();

    Collection<Region> regions(@NotNull UUID worldId);

    Collection<PersistentRegion> persistentRegions();

    Collection<PersistentRegion> persistentRegions(@NotNull UUID worldId);

    Region globalRegion(@NotNull UUID worldId);

    Collection<AreaRegion> intersecting(Area area);

    Collection<AreaRegion> intersecting(AreaRegion region);

    Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point position);

    Collection<Region> regionsAt(@NotNull Location position);

    CompletableFuture<Void> remove(@NotNull Region region);

    CompletableFuture<Void> update(@NotNull Region region);

    void register(@NotNull Region region);

    void unregister(@NotNull Region region);

    //

    CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Area area);

    CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection);

}
