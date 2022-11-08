package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegionContainer {

    private final Map<String, Region> byName = new ConcurrentHashMap<>();
    private final UUID worldId;

    private WorldRegion worldRegion;

    public RegionContainer(UUID worldId) {
        this.worldId = worldId;
    }

    public UUID worldId() {
        return worldId;
    }

    public WorldRegion worldRegion() {
        return worldRegion;
    }

    public void setWorldRegion(WorldRegion worldRegion) {
        if ( !worldRegion.worldId().equals(worldId) ) {
            throw new IllegalArgumentException("The world region is not for this world.");
        }
        this.worldRegion = worldRegion;
    }

    public Region findRegion(@NotNull String name) {
        return byName.get(name);
    }

    public void remove(Region region) {
        byName.remove(region.name(), region);
    }

    public void add(Region region) {
        byName.put(region.name(), region);
    }

    public Collection<Region> regionsAt(@NotNull Point point) {
        return Stream.concat(
                        Stream.of(worldRegion),
                        byName.values().stream()
                                .filter(rg -> rg.contains(point)))
                .collect(Collectors.toUnmodifiableSet());
    }

}
