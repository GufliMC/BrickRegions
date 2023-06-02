package com.guflimc.brick.regions.common;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.brick.regions.common.domain.*;
import com.guflimc.brick.regions.common.engine.RegionEngine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractRegionManager<P> implements RegionManager<P> {

    private final Map<P, Selection> selections = new ConcurrentHashMap<>();
    private final RegionEngine regionEngine = new RegionEngine();

    private final BrickRegionsDatabaseContext databaseContext;

    protected AbstractRegionManager(BrickRegionsDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    public void loadWorld(@NotNull UUID worldId) {
        List<DKeyedRegion> regions = databaseContext
                .findAllWhereAsync(DKeyedRegion.class, "worldId", worldId).join();

        DWorldRegion region = (DWorldRegion) regions.stream()
                .filter(rg -> rg instanceof Region.World)
                .findFirst().orElseGet(() -> {
                    DWorldRegion rg = new DWorldRegion(worldId);
                    rg.setAttribute(RegionAttributeKey.ENTRANCE_TITLE, Component.text("Wilderness", NamedTextColor.GREEN));
                    databaseContext.persistAsync(rg).join();
                    return rg;
                });

        regions.remove(region);

        regionEngine.addContainer(region);
        regions.forEach(regionEngine::addRegion);
    }

    // SELECTION

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

    // REGIONS

    @Override
    public Optional<Region.Keyed> region(@NotNull UUID id) {
        return regionEngine.region(id);
    }


    @Override
    public Optional<Region.Keyed> region(@NotNull UUID worldId, @NotNull String name) {
        return regionEngine.region(worldId, name);
    }

    @Override
    public Collection<Region> regions() {
        return regionEngine.regions();
    }

    @Override
    public <R extends Region> Collection<R> regions(@NotNull Class<R> type) {
        return regions().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <R extends Region, R1 extends Region> Collection<R> regions(@NotNull Class<R> type, @NotNull Class<R1> type1) {
        return regions().stream()
                .filter(type::isInstance)
                .filter(type1::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <R extends Region, R1 extends Region, R2 extends Region> Collection<R> regions(@NotNull Class<R> type, @NotNull Class<R1> type1, @NotNull Class<R2> type2) {
        return regions().stream()
                .filter(type::isInstance)
                .filter(type1::isInstance)
                .filter(type2::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<Region> regions(@NotNull UUID worldId) {
        return regionEngine.regions(worldId);
    }

    @Override
    public Region.World world(@NotNull UUID worldId) {
        return regionEngine.world(worldId);
    }

    @Override
    public <R extends Region> Collection<R> regions(@NotNull UUID worldId, @NotNull Class<R> type) {
        return regions(worldId).stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <R extends Region, R1 extends Region> Collection<R> regions(@NotNull UUID worldId, @NotNull Class<R> type, @NotNull Class<R1> type1) {
        return regions(worldId).stream()
                .filter(type::isInstance)
                .filter(type1::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <R extends Region, R1 extends Region, R2 extends Region> Collection<R> regions(@NotNull UUID worldId, @NotNull Class<R> type, @NotNull Class<R1> type1, @NotNull Class<R2> type2) {
        return regions(worldId).stream()
                .filter(type::isInstance)
                .filter(type1::isInstance)
                .filter(type2::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    //

    @Override
    public Collection<Region> intersecting(@NotNull UUID worldId, @NotNull Shape3 shape) {
        return regions(worldId).stream()
                .filter(rg -> rg instanceof Region.Shaped rs && shape.intersects(rs.shape()))
                .filter(rg -> !(rg instanceof Region.Activateable ra) || ra.active())
                .toList();
    }

//    @Override
//    public Collection<TileGroup> intersecting(@NotNull UUID worldId, @NotNull Shape2 shape) {
//        return regions(worldId).stream()
//                .filter(rg -> rg instanceof TileRegion)
//                .map(rg -> (TileRegion) rg)
//                .flatMap(rg -> rg.intersecting(shape).stream())
//                .toList();
//    }

//    @Override
//    public Collection<Region> localitiesAt(@NotNull UUID worldId, @NotNull Point3 position) {
//        return localitiesAt(new Location(worldId, position));
//    }
//
//    @Override
//    public Collection<Region> localitiesAt(@NotNull Location point) {
//        Collection<Region> regions = regionsAt(point);
//        List<Region> localities = new ArrayList<>(regions);
//
//        // include tile groups
//        regions.stream().filter(TileRegion.class::isInstance)
//                .map(TileRegion.class::cast)
//                .map(rg -> rg.groupAt(point).orElse(null))
//                .filter(Objects::nonNull)
//                .forEach(localities::add);
//
//        return localities;
//    }

    @Override
    public Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point3 position) {
        return regionsAt(new Location(worldId, position));
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Location position) {
        return regionEngine.regionsAt(position)
                .stream()
                .filter(rg -> !(rg instanceof Region.Activateable ra) || ra.active())
                .collect(Collectors.toList());
    }

//    @Override
//    public Optional<TileRegion> tileRegionAt(@NotNull Location location) {
//        return regionsAt(location).stream()
//                .filter(TileRegion.class::isInstance)
//                .map(TileRegion.class::cast)
//                .findFirst();
//    }
//
//    @Override
//    public Optional<TileGroup> tileGroupAt(@NotNull Location location) {
//        return tileRegionAt(location).flatMap(tr -> tr.groupAt(location));
//    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Region region) {
        if (region instanceof DWorldRegion || region instanceof DTileGroup) {
            throw new IllegalArgumentException("The given region cannot be deleted.");
        }
        if (!(region instanceof DRegion)) {
            throw new IllegalArgumentException("The given region cannot be deleted.");
        }

        regionEngine.removeRegion(region);
        EventManager.INSTANCE.onDelete(region);
        return databaseContext.removeAsync(region);
    }

    @Override
    public CompletableFuture<Void> persist(@NotNull Region region) {
        if (!(region instanceof DRegion)) {
            throw new IllegalArgumentException("The given region cannot be deleted.");
        }
        return databaseContext.persistAsync(region);
    }

//    @Override
//    public <T extends Region> CompletableFuture<Void> persist(@NotNull Collection<T> localities) {
//        return databaseContext
//                .persistAsync((Collection) localities)
//                .thenRun(() -> localities.forEach(EventManager.INSTANCE::onSave));
//    }

    @Override
    public void register(@NotNull Region region) {
        if (region instanceof DRegion) {
            throw new IllegalArgumentException("The given region cannot be registered.");
        }
        if (regionEngine.regions().contains(region)) {
            return;
        }

        regionEngine.addRegion(region);
        EventManager.INSTANCE.onRegister(region);
    }

    @Override
    public void unregister(@NotNull Region region) {
        if (region instanceof DRegion) {
            throw new IllegalArgumentException("The given region cannot be unregistered.");
        }

        regionEngine.removeRegion(region);
        EventManager.INSTANCE.onUnregister(region);
    }

    //

    @Override
    public <T extends Region.Keyed & Region.Shaped> CompletableFuture<T> create(@NotNull String name, @NotNull UUID worldId, @NotNull Shape3 shape) {
        DShapeRegion region = new DShapeRegion(worldId, name, shape);
        return databaseContext.persistAsync(region).thenApply((n) -> {
            regionEngine.addRegion(region);
            EventManager.INSTANCE.onCreate(region);
            return (T) region;
        });
    }

    @Override
    public <T extends Region.Keyed & Region.Shaped> CompletableFuture<T> create(@NotNull String name, @NotNull Selection selection) {
        return create(name, selection.worldId(), selection.shape());
    }

    @Override
    public <T extends Region.Keyed & TileRegion> CompletableFuture<T> createHexagonTileRegion(@NotNull String name, @NotNull UUID worldId, int radius) {
        DHexagonTileRegion region = new DHexagonTileRegion(worldId, name, radius);
        return databaseContext.persistAsync(region).thenApply((n) -> {
            regionEngine.addRegion(region);
            EventManager.INSTANCE.onCreate(region);
            return (T) region;
        });
    }

}
