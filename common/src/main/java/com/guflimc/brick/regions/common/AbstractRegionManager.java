package com.guflimc.brick.regions.common;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.*;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableRegion;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import com.guflimc.brick.regions.api.domain.tile.Tile;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
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

public abstract class AbstractRegionManager<P> implements RegionManager<P> {

    private final Map<P, Selection> selections = new ConcurrentHashMap<>();
    private final RegionEngine regionEngine = new RegionEngine();

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
                    region.setAttribute(LocalityAttributeKey.ENTRANCE_TITLE, Component.text("Wilderness", NamedTextColor.GREEN));
                    databaseContext.persistAsync(region).join();
                    return region;
                });

        regions.remove(worldRegion);

        regionEngine.addContainer(worldId, worldRegion);
        regions.forEach(regionEngine::add);
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
    public Optional<Region> findRegion(@NotNull UUID id) {
        return regionEngine.findRegion(id);
    }

    @Override
    public Optional<Region> findRegion(@NotNull UUID worldId, @NotNull String name) {
        return regionEngine.findRegion(worldId, name);
    }

    @Override
    public Collection<Region> regions() {
        return regionEngine.regions();
    }

    @Override
    public Collection<Region> regions(@NotNull UUID worldId) {
        return regions().stream()
                .filter(rg -> rg.worldId().equals(worldId))
                .toList();
    }

    @Override
    public Collection<ModifiableRegion> persistentRegions() {
        return regionEngine.regions().stream()
                .filter(ModifiableRegion.class::isInstance)
                .map(ModifiableRegion.class::cast)
                .toList();
    }

    @Override
    public Collection<ModifiableRegion> persistentRegions(@NotNull UUID worldId) {
        return persistentRegions().stream()
                .filter(rg -> rg.worldId().equals(worldId))
                .toList();
    }

    @Override
    public WorldRegion worldRegion(@NotNull UUID worldId) {
        return regionEngine.worldRegion(worldId);
    }

    @Override
    public Collection<ShapeRegion> intersecting(@NotNull UUID worldId, @NotNull Shape3 shape) {
        return regions(worldId).stream()
                .filter(rg -> rg instanceof ShapeRegion)
                .map(rg -> (ShapeRegion) rg)
                .filter(rg -> shape.intersects(rg.shape()))
                .toList();
    }

    @Override
    public Collection<ShapeRegion> intersecting(@NotNull ShapeRegion region) {
        return intersecting(region.worldId(), region.shape());
    }

    @Override
    public Collection<TileGroup> intersecting(@NotNull UUID worldId, @NotNull Shape2 shape) {
        return regions(worldId).stream()
                .filter(rg -> rg instanceof TileRegion)
                .map(rg -> (TileRegion) rg)
                .flatMap(rg -> rg.intersecting(shape).stream())
                .toList();
    }

    @Override
    public <T extends Locality> CompletableFuture<Void> save(@NotNull Collection<T> localities) {
        return null;
    }

    @Override
    public Collection<Locality> localitiesAt(@NotNull UUID worldId, @NotNull Point3 position) {
        return localitiesAt(new Location(worldId, position));
    }

    @Override
    public Collection<Locality> localitiesAt(@NotNull Location point) {
        Collection<Region> regions = regionsAt(point);
        List<Locality> localities = new ArrayList<>(regions);

        // include tile groups
        regions.stream().filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .map(rg -> rg.groupAt(point).orElse(null))
                .filter(Objects::nonNull)
                .forEach(localities::add);

        return localities;
    }

    @Override
    public Collection<Region> regionsAt(@NotNull UUID worldId, @NotNull Point3 position) {
        return regionsAt(new Location(worldId, position));
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Location position) {
        return regionEngine.regionsAt(position);
    }

    @Override
    public Optional<TileRegion> tileRegionAt(@NotNull Location location) {
        return regionsAt(location).stream()
                .filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .findFirst();
    }

    @Override
    public Optional<TileGroup> tileGroupAt(@NotNull Location location) {
        return tileRegionAt(location).flatMap(tr -> tr.groupAt(location));
    }

    @Override
    public CompletableFuture<Void> delete(@NotNull Locality locality) {
        if (!(locality instanceof DModifiableLocality)) {
            throw new IllegalArgumentException("The given locality is not persisted by BrickRegions.");
        }
        if ( locality instanceof DWorldRegion) {
            throw new IllegalArgumentException("Cannot delete the global region.");
        }
        if (locality instanceof DRegion region) {
            regionEngine.remove(region);
            EventManager.INSTANCE.onDelete(region);
        }
        return databaseContext.removeAsync(locality);
    }

    @Override
    public CompletableFuture<Void> save(@NotNull Locality locality) {
        if (!(locality instanceof DModifiableLocality)) {
            throw new IllegalArgumentException("The given locality is not persisted by BrickRegions.");
        }
        return databaseContext.persistAsync(locality);
    }

    @Override
    public void register(@NotNull Region region) {
        if (region instanceof DRegion) {
            throw new IllegalArgumentException("Only transient regions can be registered.");
        }
        if (regionEngine.findRegion(region.id()).isPresent()) {
            return;
        }

        regionEngine.add(region);
        EventManager.INSTANCE.onRegister(region);
    }

    @Override
    public void unregister(@NotNull Region region) {
        if (region instanceof DRegion) {
            throw new IllegalArgumentException("Only transient regions can be unregistered.");
        }

        regionEngine.remove(region);
        EventManager.INSTANCE.onUnregister(region);
    }

    //

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Shape3 shape) {
        DShapeRegion region = new DShapeRegion(worldId, name, shape);
        return databaseContext.persistAsync(region).thenApply((n) -> {
            regionEngine.add(region);
            EventManager.INSTANCE.onCreate(region);
            return region;
        });
    }

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull Selection selection) {
        return create(name, selection.worldId(), selection.shape());
    }

    @Override
    public CompletableFuture<TileRegion> createHexagonTileRegion(@NotNull String name, @NotNull UUID worldId, int radius) {
        DHexagonTileRegion region = new DHexagonTileRegion(worldId, name, radius);
        return databaseContext.persistAsync(region).thenApply((n) -> {
            regionEngine.add(region);
            EventManager.INSTANCE.onCreate(region);
            return region;
        });
    }

}
