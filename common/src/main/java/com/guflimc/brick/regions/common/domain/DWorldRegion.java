package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.WorldRegion;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class DWorldRegion extends DRegion implements WorldRegion {

    public DWorldRegion() {
        super();
    }

    public DWorldRegion(UUID worldId, String name) {
        super(worldId, name);
        this.setPriority(0);
    }

    @Override
    public boolean contains(Point3 point) {
        return WorldRegion.super.contains(point);
    }
}
