package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;

public interface ShapeRegion extends Region {

    Shape3 shape();

    @Override
    default boolean contains(Point3 point) {
        return shape().contains(point);
    }
}
