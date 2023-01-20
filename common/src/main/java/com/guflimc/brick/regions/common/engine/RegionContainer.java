package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegionContainer {

    private final Map<String, Region> byName = new ConcurrentHashMap<>();
    private final UUID worldId;
    private final WorldRegion worldRegion;

    public RegionContainer(UUID worldId, WorldRegion worldRegion) {
        if (!worldRegion.worldId().equals(worldId)) {
            throw new IllegalArgumentException("The world region is not for this world.");
        }

        this.worldId = worldId;
        this.worldRegion = worldRegion;
        byName.put(worldRegion.name(), worldRegion);
    }

    public UUID worldId() {
        return worldId;
    }

    public WorldRegion worldRegion() {
        return worldRegion;
    }

    //

    public void remove(Region region) {
        if (region.equals(worldRegion)) {
            return;
        }
        byName.remove(region.name(), region);
    }

    public void add(Region region) {
        byName.put(region.name(), region);
    }

    //

    public Collection<Region> regions() {
        return Collections.unmodifiableCollection(byName.values());
    }

    public Optional<Region> findRegion(@NotNull String name) {
        return Optional.ofNullable(byName.get(name));
    }

    public Collection<Region> regionsAt(@NotNull Point3 point) {
        return byName.values().stream()
                .filter(rg -> rg.contains(point))
                .collect(Collectors.toUnmodifiableSet());
    }

}
