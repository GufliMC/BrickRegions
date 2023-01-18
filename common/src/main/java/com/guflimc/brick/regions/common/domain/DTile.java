package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.math.database.Shape2Converter;
import com.guflimc.brick.regions.api.domain.Tile;

import javax.persistence.*;

@Entity
public class DTile extends DLocality implements Tile {

    @Column(name = "tile_region_id")
    @ManyToOne(targetEntity = DShapeRegion.class, fetch = FetchType.EAGER)
    private DShapeRegion region;

    @Column(name = "tile_position", length = 1024)
    @Convert(converter = Point2Converter.class)
    private Point2 position;

    @Column(name = "tile_polygon", length = 2048)
    @Convert(converter = Shape2Converter.class)
    private Polygon polygon;

    public DTile() {
    }

    public DTile(DShapeRegion region, Point2 position, Polygon polygon) {
        super(region.worldId());
        this.region = region;
        this.position = position;
        this.polygon = polygon;

        this.region.tiles.add(this);
    }

    @Override
    public Point2 position() {
        return position;
    }

    @Override
    public Polygon polygon() {
        return polygon;
    }

    @Override
    public boolean contains(Point3 point) {
        return polygon.contains(new Vector2(point.x(), point.z()));
    }
}
