package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape2d.RegularHexagon;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.database.GsonTools;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.math.database.Shape2Converter;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.TileRegion;

import javax.persistence.*;
import java.util.Collection;
import java.util.Optional;

@Entity
public class DTile extends DLocality implements Tile {

    @Column(name = "tile_parent_id")
    @ManyToOne(targetEntity = DHexagonTileRegion.class, fetch = FetchType.EAGER)
    private DHexagonTileRegion parent;

    @Column(name = "tile_position", length = 1024)
    @Convert(converter = Point2Converter.class)
    private Point2 position;

    @Column(name = "tile_shape", length = 2048)
    @Convert(converter = Shape2Converter.class)
    private Shape2 shape;

    public DTile() {
    }

    DTile(DHexagonTileRegion region, Point2 position, Shape2 shape) {
        super(region.worldId());
        this.parent = region;
        this.position = position;
        this.shape = shape;
    }

    @Override
    public Point2 position() {
        return position;
    }

    @Override
    public Shape2 shape() {
        return shape;
    }

    @Override
    public TileRegion parent() {
        return parent;
    }

    @Override
    public Collection<Tile> adjacent() {
        return parent.adjacent(this);
    }

    @Override
    public boolean contains(Point3 point) {
        return contains(new Vector2(point.x(), point.z()));
    }

    public boolean contains(Point2 point) {
        point = new Vector2(point.blockX(), point.blockY());
        return shape.contains(point);
    }

    @Override
    public <U> Optional<U> attribute(LocalityAttributeKey<U> key) {
        Optional<U> opt = super.attribute(key);
        if ( opt.isPresent() ) {
            return opt;
        }
        return parent.attribute(key);
    }

}
