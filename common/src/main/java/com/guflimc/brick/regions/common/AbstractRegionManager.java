package com.guflimc.brick.regions.common;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector;
import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.common.domain.DAreaRegion;
import com.guflimc.brick.regions.common.engine.RegionEngine;
import com.guflimc.brick.regions.common.engine.ZonedRegionEngine;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegionManager<P> implements RegionManager<P> {

    private final Map<P, Selection> selections = new ConcurrentHashMap<>();
    private final RegionEngine engine = new ZonedRegionEngine(new Vector(0, 0, 0), 500);

    private final BrickRegionsDatabaseContext databaseContext;

    protected AbstractRegionManager(BrickRegionsDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        databaseContext.findAllAsync(DAreaRegion.class).join()
                .forEach(engine::add);
    }

    @Override
    public void clearSelection(@NotNull P subject) {
        selections.remove(subject);
    }

    @Override
    public Optional<Selection> selection(@NotNull P subject) {
        return Optional.ofNullable(selections.get(subject));
    }

    @Override
    public void setSelection(@NotNull P subject, @NotNull Selection selection) {
        selections.put(subject, selection);
    }

    @Override
    public Optional<Region> findRegion(@NotNull UUID id) {
        return engine.findRegion(id);
    }

    @Override
    public Optional<Region> findRegion(@NotNull String name) {
        return engine.findRegion(name);
    }

    @Override
    public Collection<Region> regions() {
        return engine.regions();
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Point point) {
        return engine.regionsAt(point);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Region region) {
        engine.remove(region);
        return databaseContext.removeAsync(region);
    }

    @Override
    public CompletableFuture<Void> update(@NotNull Region region) {
        return databaseContext.mergeAsync(region).thenRun(() -> {
        });
    }

    //

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull Area area) {
        DAreaRegion region = new DAreaRegion(name, area);
        return databaseContext.persistAsync(region).thenApply((n) -> {
            engine.add(region);
            return region;
        });
    }

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection) {
        return create(name, selection.area());
    }

}
