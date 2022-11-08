package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.database.api.AreaConverter;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.PersistentAreaRegion;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.Region;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("AREA")
@Table(name = "area_regions")
public class DAreaRegion extends DRegion implements PersistentAreaRegion {

    @Convert(converter = AreaConverter.class)
    @Column(nullable = false)
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
        return  PersistentAreaRegion.super.contains(point);
    }
}
