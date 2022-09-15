package com.guflimc.brick.regions.api;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector2;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.api.selection.Selection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

public interface RegionManager<S> {

    //

    void clearSelection(@NotNull S subject);

    Optional<Selection> selection(@NotNull S subject);

    void setSelection(@NotNull S subject, @NotNull Selection selection);

    //

    Optional<Region> findRegion(@NotNull UUID id);

    Optional<Region> findRegion(@NotNull String name);

    Collection<Region> regions();

    Collection<PersistentRegion> persistentRegions();

    Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point position);

    Collection<Region> regionsAt(@NotNull Location position);

    CompletableFuture<Void> remove(@NotNull Region region);

    CompletableFuture<Void> update(@NotNull Region region);

    void register(@NotNull Region region);

    void unregister(@NotNull Region region);

    //

    CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Area area);

    CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection);

    //

    void addRule(PersistentRegion region, RuleStatus status, RuleTarget<S> target, RuleType... type);

    void addRule(PersistentRegion region, int priority, RuleStatus status, RuleTarget<S> target, RuleType... type);

    //

    default boolean isAllowed(S subject, RuleType type, UUID worldId, Point point) {
        return isAllowed(subject, type, regionsAt(new Location(worldId, point)));
    }

    boolean isAllowed(S subject, RuleType type, Collection<Region> regions);

}
