package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface WorldRegionEngine {

    UUID worldId();

    void remove(Region region);

    void add(Region region);

    Collection<Region> regionsAt(@NotNull Point position);


}
