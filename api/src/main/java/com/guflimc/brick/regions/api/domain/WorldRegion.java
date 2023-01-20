package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;

import java.util.Objects;

public interface WorldRegion extends Region {

    @Override
    default boolean contains(Point3 point) {
        return !(point instanceof Location loc) || Objects.equals(loc.worldId(), worldId());
    }
}
