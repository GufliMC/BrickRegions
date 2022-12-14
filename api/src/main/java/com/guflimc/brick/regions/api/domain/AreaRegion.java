package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;

public interface AreaRegion extends Region {

    Area area();

    @Override
    default boolean contains(Point point) {
        return area().contains(point);
    }
}
