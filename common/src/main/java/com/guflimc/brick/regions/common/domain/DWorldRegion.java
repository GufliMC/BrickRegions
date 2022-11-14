package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import javax.persistence.*;

import java.util.UUID;

@Entity
//@DiscriminatorValue("global")
@Table(name = "world_regions")
public class DWorldRegion extends DRegion implements WorldRegion {

    public DWorldRegion() {
        super();
    }

    public DWorldRegion(UUID worldId, String name) {
        super(worldId, name);
        this.setPriority(0);
    }

    @Override
    public boolean contains(Point point) {
        return WorldRegion.super.contains(point);
    }
}
