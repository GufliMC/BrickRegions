package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.pos3.Vector3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;

public interface ShapeRegion extends Region {

    Shape3 shape();

    @Override
    default boolean contains(Point3 point) {
        point = new Vector3(point.blockX(), point.blockY(), point.blockZ());
        return shape().contains(point);
    }
}
