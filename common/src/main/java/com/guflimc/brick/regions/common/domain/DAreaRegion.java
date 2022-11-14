package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.orm.ebean.converters.JakartaWrapperConverter;
import com.guflimc.brick.regions.api.domain.PersistentAreaRegion;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
//@DiscriminatorValue("area")
@Table(name = "area_regions")
public class DAreaRegion extends DRegion implements PersistentAreaRegion {

    @Convert(converter = AreaConverter.class)
    @Column(nullable = false, length = 8192)
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

    public static class AreaConverter extends JakartaWrapperConverter<Area, String> {
        public AreaConverter() {
            super(new com.guflimc.brick.maths.database.api.AreaConverter());
        }
    }
}
