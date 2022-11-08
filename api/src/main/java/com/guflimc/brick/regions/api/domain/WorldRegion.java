package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.pos.Point;

public interface WorldRegion extends PersistentRegion {

    @Override
    default boolean contains(Point point) {
        return true;
    }
}
