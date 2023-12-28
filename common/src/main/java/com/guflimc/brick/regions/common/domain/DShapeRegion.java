package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.math.database.Shape3Converter;
import com.guflimc.brick.regions.api.domain.Region;
import io.ebean.annotation.DbDefault;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class DShapeRegion extends DNamedPropertyRegion implements Region.Shaped {

    @Convert(converter = Shape3Converter.class)
    @Column(name = "shaperegion_shape", length = 2048)
    @DbDefault("null")
    private Shape3 shape;

    public DShapeRegion() {
        super();
    }

    public DShapeRegion(@NotNull UUID worldId, @NotNull String name, @NotNull Shape3 shape) {
        super(worldId, name);
        this.shape = shape;
    }

    @Override
    public Shape3 shape() {
        return shape;
    }

    @Override
    public boolean contains(@NotNull Point3 point) {
        return Region.Shaped.super.contains(point);
    }
}
