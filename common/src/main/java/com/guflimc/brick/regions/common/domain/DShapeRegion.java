package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.RectPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.math.database.Shape3Converter;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableShapeRegion;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.TileRegion;
import io.ebean.annotation.DbDefault;

import javax.persistence.*;
import java.util.*;

@Entity
public class DShapeRegion extends DRegion implements ModifiableShapeRegion, TileRegion {

    @Convert(converter = Shape3Converter.class)
    @Column(name = "shaperegion_shape", length = 2048)
    @DbDefault("null")
    private Shape3 shape;

    @OneToMany(targetEntity = DTile.class, mappedBy = "region",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<DTile> tiles = new ArrayList<>();

    @Column(name = "tiledregion_tile_radius")
    @DbDefault("0")
    private int tileRadius;

    private transient int tileWidth = -1;

    private transient final Map<Vector2, DTile> tileMap = new HashMap<>();

    public DShapeRegion() {
        super();
    }

    public DShapeRegion(UUID worldId, String name, Shape3 shape) {
        super(worldId, name);
        this.shape = shape;
    }

    @PostLoad
    public void postLoad() {
        tileWidth = (int) Math.round((tileRadius / 2d) * Math.sqrt(3) * 2);
        tiles.forEach(tile -> tileMap.put((Vector2) tile.position(), tile));
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

    @Override
    public Tile tileAt(Point3 point) {
        int tileZ = (int) Math.round(point.z() / (int) (tileRadius / 2d * 3));
        double x = point.x();
        if ( tileZ % 2 != 0 ) {
            x -= tileWidth / 2d;
        }
        int tileX = (int) Math.round(x / tileWidth);

        Tile tile = tileMap.get(new Vector2(tileX, tileZ));;
        if ( point.z() < tileZ * (tileRadius / 2d) || point.z() > tileZ * tileRadius ) {
            return tile;
        }

        // correct guess
        if ( tile != null && tile.contains(point) ) {
            return tile;
        }

        Tile left = tileMap.get(new Vector2(tileX, tileZ - 1));
        if ( left != null && left.contains(point) ) {
            return left;
        }

        Tile right = tileMap.get(new Vector2(tileX + 1, tileZ - 1));
        if ( right != null && right.contains(point) ) {
            return right;
        }

        return null;
    }

    @Override
    public Collection<Tile> tiles() {
        return Collections.unmodifiableList(tiles);
    }

    public void generateTiles(int tileRadius) {
        Rectangle bounds;
        if (shape instanceof RectPrism rp) {
            bounds = rp.rectangle();
        } else if (shape instanceof PolyPrism pp) {
            bounds = pp.polygon().boundingBox();
        } else {
            throw new IllegalArgumentException("Unsupported shape type: " + shape.getClass().getName());
        }

        tiles.clear();

        this.tileRadius = tileRadius;
        this.tileWidth = (int) Math.round((tileRadius / 2d) * Math.sqrt(3) * 2);

        int minTileX = (int) Math.ceil(bounds.min().x() / (double) tileWidth);
        int maxTileX = (int) Math.floor(bounds.max().x() / (double) tileWidth);
        int minTileZ = (int) Math.ceil(bounds.min().y() / (tileRadius / 2d * 3));
        int maxTileZ = (int) Math.floor(bounds.max().y() / (tileRadius / 2d * 3));

        int tileRadiusOffset = (int) (tileRadius / 2d * 3);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileZ = minTileZ; tileZ <= maxTileZ; tileZ++) {
                double x = tileX * tileWidth;
                double z = tileZ * tileRadiusOffset;
                if ( tileZ % 2 != 0 ) {
                    x += tileWidth / 2d;
                }

                if (x - (tileWidth / 2d) < bounds.min().x()
                        || x + (tileWidth / 2d) > bounds.max().x()
                        || z - tileRadiusOffset < bounds.min().y()
                        || z + tileRadiusOffset > bounds.max().y()) {
                    continue;
                }

                new DTile(this, new Vector2(tileX, tileZ), hexagonAt(x, z, tileRadius, tileWidth));
            }
        }
    }

    private Polygon hexagonAt(double x, double z, int tileRadius, int tileWidth) {
        List<Vector2> vertices = new ArrayList<>();
        vertices.add(new Vector2(x, z + tileRadius));
        vertices.add(new Vector2(x + (tileWidth / 2d), z + (tileRadius / 2d)));
        vertices.add(new Vector2(x + (tileWidth / 2d), z - (tileRadius / 2d)));
        vertices.add(new Vector2(x, z - tileRadius));
        vertices.add(new Vector2(x - (tileWidth / 2d), z - (tileRadius / 2d)));
        vertices.add(new Vector2(x - (tileWidth / 2d), z + (tileRadius / 2d)));
        return new Polygon(vertices);
    }
}
