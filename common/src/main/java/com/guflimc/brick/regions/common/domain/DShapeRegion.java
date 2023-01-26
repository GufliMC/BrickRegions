package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.math.database.Shape3Converter;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableShapeRegion;
import io.ebean.annotation.DbDefault;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class DShapeRegion extends DRegion implements ModifiableShapeRegion {

    @Convert(converter = Shape3Converter.class)
    @Column(name = "shaperegion_shape", length = 2048)
    @DbDefault("null")
    private Shape3 shape;

    public DShapeRegion() {
        super();
    }

    public DShapeRegion(UUID worldId, String name, Shape3 shape) {
        super(worldId, name);
        this.shape = shape;
    }

    @Override
    public Shape3 shape() {
        return shape;
    }

    @Override
    public void setShape(Shape3 shape) {
        this.shape = shape;
    }

    @Override
    public boolean contains(Point3 point) {
        return ModifiableShapeRegion.super.contains(point);
    }
}
