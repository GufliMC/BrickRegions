package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;

public interface PersistentWorldRegion extends PersistentRegion {

    @Override
    default boolean contains(Point3 point) {
        return true;
    }
}
