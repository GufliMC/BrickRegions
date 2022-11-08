package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;

public interface PersistentAreaRegion extends PersistentRegion, AreaRegion{

    void setArea(Area area);

    @Override
    default boolean contains(Point point) {
        return AreaRegion.super.contains(point);
    }
}
