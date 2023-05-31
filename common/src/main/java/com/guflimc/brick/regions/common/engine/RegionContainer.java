package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.region.Region;
import com.guflimc.brick.regions.api.domain.region.RegionKey;
import com.guflimc.brick.regions.api.domain.region.WorldRegion;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegionContainer {

    private final Map<RegionKey, Region> byName = new ConcurrentHashMap<>();
    private final UUID worldId;
    private final WorldRegion worldRegion;

    public RegionContainer(UUID worldId, WorldRegion worldRegion) {
        if (!worldRegion.worldId().equals(worldId)) {
            throw new IllegalArgumentException("The world region is not for this world.");
        }

        this.worldId = worldId;
        this.worldRegion = worldRegion;
        byName.put(worldRegion.key(), worldRegion);
    }

    public UUID worldId() {
        return worldId;
    }

    public WorldRegion worldRegion() {
        return worldRegion;
    }

    //

    public void removeRegion(Region region) {
        if (region.equals(worldRegion)) {
            return;
        }
        byName.remove(region.key(), region);
    }

    public void addRegion(Region region) {
        byName.put(region.key(), region);
    }

    //

    public Collection<Region> regions() {
        return Collections.unmodifiableCollection(byName.values());
    }

    public Optional<Region> region(@NotNull RegionKey key) {
        return Optional.ofNullable(byName.get(key));
    }

    public Collection<Region> regionsAt(@NotNull Point3 point) {
        return byName.values().stream()
                .filter(rg -> rg.contains(point))
                .collect(Collectors.toUnmodifiableSet());
    }

}
