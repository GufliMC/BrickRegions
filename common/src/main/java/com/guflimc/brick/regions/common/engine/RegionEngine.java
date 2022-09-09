package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface RegionEngine {

    void remove(Region region);

    void add(Region region);

    Optional<Region> findRegion(@NotNull UUID id);

    Optional<Region> findRegion(@NotNull String name);

    Collection<Region> regions();

    Collection<Region> regionsAt(@NotNull Point position);


}
