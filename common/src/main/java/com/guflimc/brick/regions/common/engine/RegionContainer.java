package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegionContainer {

    private final Set<Region> regions = new CopyOnWriteArraySet<>();
    private final Map<String, Region.Keyed> byName = new ConcurrentHashMap<>();

    private final Region.World region;

    public <T extends Region.World & Region.Keyed> RegionContainer(UUID worldId, T region) {
        if (!region.worldId().equals(worldId)) {
            throw new IllegalArgumentException("The world region is not for this world.");
        }

        this.region = region;
        byName.put(region.name(), region);
    }

    public Region.World world() {
        return region;
    }

    //

    public void removeRegion(Region region) {
        if (region.equals(this.region)) {
            return;
        }

        regions.remove(region);

        if (region instanceof Region.Keyed rk)
            byName.remove(rk.name(), rk);
    }

    public void addRegion(Region region) {
        regions.add(region);

        if (region instanceof Region.Keyed rk)
            byName.put(rk.name(), rk);
    }

    //

    public Collection<Region> regions() {
        return Stream.concat(regions.stream(), Stream.of(region))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Optional<Region.Keyed> region(@NotNull String name) {
        return Optional.ofNullable(byName.get(name));
    }

    public Collection<Region> regionsAt(@NotNull Point3 point) {
        return regions().stream()
                .filter(rg -> rg.contains(point))
                .collect(Collectors.toUnmodifiableSet());
    }

}
