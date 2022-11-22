package com.guflimc.brick.regions.common;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.PolyArea;
import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.common.domain.DAreaRegion;
import com.guflimc.brick.regions.common.domain.DRegion;
import com.guflimc.brick.regions.common.domain.DWorldRegion;
import com.guflimc.brick.regions.common.engine.RegionEngine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegionManager<P> implements RegionManager<P> {

    private final Map<P, Selection> selections = new ConcurrentHashMap<>();
    private final RegionEngine engine = new RegionEngine();

    private final BrickRegionsDatabaseContext databaseContext;

    protected AbstractRegionManager(BrickRegionsDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    public void loadWorld(UUID worldId) {
        List<DRegion> regions = databaseContext
                .findAllWhereAsync(DRegion.class, "worldId", worldId).join();

        WorldRegion worldRegion = (WorldRegion) regions.stream()
                .filter(rg -> rg instanceof WorldRegion)
                .findFirst().orElseGet(() -> {
                    DWorldRegion region = new DWorldRegion(worldId, "__global__");
                    region.setDisplayName(Component.text("Wilderness", NamedTextColor.GREEN));
                    databaseContext.persistAsync(region).join();
                    return region;
                });

        regions.remove(worldRegion);

        engine.addContainer(worldId, worldRegion);
        regions.forEach(engine::add);
    }

    //

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

    //

    @Override
    public Optional<Region> findRegion(@NotNull UUID id) {
        return engine.findRegion(id);
    }

    @Override
    public Optional<Region> findRegion(@NotNull UUID worldId, @NotNull String name) {
        return engine.findRegion(worldId, name);
    }

    @Override
    public Collection<Region> regions() {
        return engine.regions();
    }

    @Override
    public Collection<Region> regions(@NotNull UUID worldId) {
        return regions().stream()
                .filter(rg -> rg.worldId().equals(worldId))
                .toList();
    }

    @Override
    public Collection<PersistentRegion> persistentRegions() {
        return engine.regions().stream()
                .filter(PersistentRegion.class::isInstance)
                .map(PersistentRegion.class::cast)
                .toList();
    }

    @Override
    public Collection<PersistentRegion> persistentRegions(@NotNull UUID worldId) {
        return persistentRegions().stream()
                .filter(rg -> rg.worldId().equals(worldId))
                .toList();
    }

    @Override
    public WorldRegion worldRegion(@NotNull UUID worldId) {
        return engine.worldRegion(worldId);
    }

    @Override
    public Collection<AreaRegion> intersecting(Area area) {
        return regions().stream()
                .filter(rg -> rg instanceof AreaRegion)
                .map(rg -> (AreaRegion) rg)
                .filter(rg -> area.intersects(rg.area()))
                .toList();
    }

    @Override
    public Collection<AreaRegion> intersecting(AreaRegion region) {
        return intersecting(region.area());
    }

    @Override
    public Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point position) {
        return engine.regionsAt(new Location(worldId, position));
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Location point) {
        return engine.regionsAt(point);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Region region) {
        if (!(region instanceof DRegion)) {
            throw new IllegalArgumentException("Only persistent regions can be removed.");
        }
        engine.remove(region);
        return databaseContext.removeAsync(region);
    }

    @Override
    public CompletableFuture<Void> update(@NotNull Region region) {
        if (!(region instanceof DRegion dr)) {
            throw new IllegalArgumentException("Only persistent regions can be saved.");
        }
        return databaseContext.persistAsync(region);
    }

    @Override
    public void register(@NotNull Region region) {
        if (region instanceof DRegion) {
            throw new IllegalArgumentException("Only transient regions can be registered.");
        }
        if ( engine.findRegion(region.id()).isPresent() ) {
            return;
        }

        engine.add(region);
    }

    @Override
    public void unregister(@NotNull Region region) {
        if (region instanceof DRegion) {
            throw new IllegalArgumentException("Only transient regions can be unregistered.");
        }

        engine.remove(region);
    }

    //

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Area area) {
        if (area instanceof PolyArea pa && !pa.isConvex()) {
            throw new IllegalArgumentException("A polygon area must be convex.");
        }
        DAreaRegion region = new DAreaRegion(worldId, name, area);
        return databaseContext.persistAsync(region).thenApply((n) -> {
            engine.add(region);
            return region;
        });
    }

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection) {
        return create(name, selection.worldId(), selection.area());
    }

}
