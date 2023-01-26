package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.math.database.Shape2Converter;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.TileRegion;

import javax.persistence.*;

@Entity
public class DTile extends DLocality implements Tile {

    @Column(name = "tile_parent_id")
    @ManyToOne(targetEntity = DTileRegion.class, fetch = FetchType.EAGER)
    private DTileRegion parent;

    @Column(name = "tile_position", length = 1024)
    @Convert(converter = Point2Converter.class)
    private Point2 position;

    @Column(name = "tile_polygon", length = 2048)
    @Convert(converter = Shape2Converter.class)
    private Polygon polygon;

    public DTile() {
    }

    DTile(DTileRegion region, Point2 position, Polygon polygon) {
        super(region.worldId());
        this.parent = region;
        this.position = position;
        this.polygon = polygon;
    }

    @Override
    public Point2 position() {
        return position;
    }

    @Override
    public Polygon shape() {
        return polygon;
    }

    @Override
    public TileRegion parent() {
        return parent;
    }

    @Override
    public boolean contains(Point3 point) {
        return contains(new Vector2(point.x(), point.z()));
    }

    public boolean contains(Point2 point) {
        return polygon.contains(point);
    }
}
