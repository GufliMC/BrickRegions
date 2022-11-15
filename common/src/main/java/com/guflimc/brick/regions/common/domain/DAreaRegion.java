package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.database.api.AreaConverter;
import com.guflimc.brick.regions.api.domain.PersistentAreaRegion;
import io.ebean.annotation.DbDefault;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class DAreaRegion extends DRegion implements PersistentAreaRegion {

    @Convert(converter = AreaConverter.class)
    @Column(length = 8192)
    @DbDefault("null")
    private Area area;

    public DAreaRegion() {
        super();
    }

    public DAreaRegion(UUID worldId, String name, Area area) {
        super(worldId, name);
        this.area = area;
    }

    @Override
    public Area area() {
        return area;
    }

    @Override
    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public boolean contains(Point point) {
        return PersistentAreaRegion.super.contains(point);
    }
}
