package com.guflimc.brick.regions.api.domain.region;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.pos3.Vector3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import org.jetbrains.annotations.NotNull;

public interface ShapeRegion extends ModifiableRegion {

    Shape3 shape();

    @Override
    default boolean contains(@NotNull Point3 point) {
        point = new Vector3(point.blockX(), point.blockY(), point.blockZ());
        return shape().contains(point);
    }

}
