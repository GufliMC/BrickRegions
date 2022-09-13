package com.guflimc.brick.regions.common.engine.zone;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class RegionZone {

    private final Set<AreaRegion> regions = new CopyOnWriteArraySet<>();

    public Collection<Region> regionsAt(@NotNull Point point) {
        return regions.stream()
                .filter(rg -> rg.area().contains(point))
                .collect(Collectors.toUnmodifiableSet());
    }

    public void add(AreaRegion region) {
        regions.add(region);
    }

    public void remove(AreaRegion region) {
        regions.remove(region);
    }
}