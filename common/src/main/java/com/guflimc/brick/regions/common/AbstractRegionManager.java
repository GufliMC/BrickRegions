package com.guflimc.brick.regions.common;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector;
import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.common.domain.DAreaRegion;
import com.guflimc.brick.regions.common.engine.RegionEngine;
import com.guflimc.brick.regions.common.engine.RegionContainer;
import com.guflimc.brick.regions.common.engine.zone.CircularRegionZoneContainer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegionManager<P> implements RegionManager<P> {

    private final Map<P, Selection> selections = new ConcurrentHashMap<>();
    private final RegionEngine engine = new RegionEngine(uuid -> new CircularRegionZoneContainer(uuid, new Vector(0, 0, 0), 500));

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
    public Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point position) {
        return engine.regionsAt(new Location(worldId, position));
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Location point) {
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
    public CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Area area) {
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

    //

    @Override
    public boolean isAllowed(P subject, RuleType type, Collection<Region> regions) {
//        return regions.stream().flatMap(rg -> rg.rules().stream()) // get all rules
//                .sorted(Comparator.comparing(Rule::priority).reversed()) // sort by priority
//                .filter(rule -> Arrays.stream(rule.ruleTypes()).anyMatch(t -> t == type)) // filter by correct type
//                .filter(rule -> rule.predicate().test(subject, null)) // filter by predicate
//                .findFirst() // first matching = high priority, matches type and matches predicate
//                .map(r -> r.status() != RuleStatus.DENY) // return false if denied
//                .orElse(true); // return true in any other case
        return true;
    }

}
