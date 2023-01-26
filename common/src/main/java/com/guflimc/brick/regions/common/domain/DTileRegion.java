package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape3d.PolyPrism;
import com.guflimc.brick.math.common.geometry.shape3d.RectPrism;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.TileRegion;
import io.ebean.annotation.DbDefault;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.*;

@Entity
public class DTileRegion extends DRegion implements TileRegion {

    @OneToMany(targetEntity = DTile.class, mappedBy = "parent",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<DTile> tiles = new ArrayList<>();

    @Column(name = "tileregion_tile_radius")
    @DbDefault("0")
    private int tileRadius;

    @Column(name = "tileregion_tile_width")
    @DbDefault("0")
    private int tileWidth;

    @Column(name = "tileregion_offset")
    @Convert(converter = Point2Converter.class)
    private Point2 offset = new Vector2(0, 0);

    private transient final Map<Vector2, DTile> tilemap = new HashMap<>();

    public DTileRegion() {
        super();
    }

    public DTileRegion(UUID worldId, String name, Shape3 shape, int tileRadius) {
        super(worldId, name);
        this.tileRadius = tileRadius;
        this.tileWidth = (int) Math.round((tileRadius / 2d) * Math.sqrt(3) * 2);

        generate(shape);
        mapTiles();
    }

    private void generate(Shape3 shape) {
        Rectangle bounds;
        if (shape instanceof RectPrism rp) {
            bounds = rp.rectangle();
        } else if (shape instanceof PolyPrism pp) {
            bounds = pp.polygon().boundingBox();
        } else {
            throw new IllegalArgumentException("Unsupported shape type: " + shape.getClass().getName());
        }

        int minTileX = (int) Math.ceil((bounds.min().x() + offset.x()) / (double) tileWidth);
        int maxTileX = (int) Math.floor((bounds.max().x() + offset.x()) / (double) tileWidth);
        int minTileZ = (int) Math.ceil((bounds.min().y() + offset.y()) / (tileRadius / 2d * 3));
        int maxTileZ = (int) Math.floor((bounds.max().y() + offset.y()) / (tileRadius / 2d * 3));

        int tileRadiusOffset = (int) (tileRadius / 2d * 3);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileZ = minTileZ; tileZ <= maxTileZ; tileZ++) {
                double x = offset.x() + tileX * tileWidth;
                double z = offset.y() + tileZ * tileRadiusOffset;
                if (tileZ % 2 != 0) {
                    x += tileWidth / 2d;
                }

                if (x - (tileWidth / 2d) < bounds.min().x()
                        || x + (tileWidth / 2d) > bounds.max().x()
                        || z - tileRadiusOffset < bounds.min().y()
                        || z + tileRadiusOffset > bounds.max().y()) {
                    continue;
                }

                tiles.add(new DTile(this, new Vector2(tileX, tileZ), hexagonAt(x, z)));
            }
        }
    }

    private Polygon hexagonAt(double x, double z) {
        List<Vector2> vertices = new ArrayList<>();
        vertices.add(new Vector2(x, z + tileRadius));
        vertices.add(new Vector2(x + (tileWidth / 2d), z + (tileRadius / 2d)));
        vertices.add(new Vector2(x + (tileWidth / 2d), z - (tileRadius / 2d)));
        vertices.add(new Vector2(x, z - tileRadius));
        vertices.add(new Vector2(x - (tileWidth / 2d), z - (tileRadius / 2d)));
        vertices.add(new Vector2(x - (tileWidth / 2d), z + (tileRadius / 2d)));
        return new Polygon(vertices);
    }

    //

    @PostLoad
    public void mapTiles() {
        tiles.forEach(tile -> tilemap.put((Vector2) tile.position(), tile));
    }

    //

    @Override
    public boolean contains(Point3 point) {
        return tileAt(point).isPresent();
    }

    @Override
    public Optional<Tile> tileAt(@NotNull Point2 point) {
        int tileZ = (int) Math.round((offset.y() + point.y()) / (int) (tileRadius / 2d * 3));
        double x = point.x();
        if (tileZ % 2 != 0) {
            x -= tileWidth / 2d;
        }
        int tileX = (int) Math.round((offset.x() + x) / tileWidth);

        DTile tile = tilemap.get(new Vector2(tileX, tileZ));

        // is in center rectangle of tile
        if (point.y() < tileZ * (tileRadius / 2d) || point.y() > tileZ * tileRadius) {
            return Optional.ofNullable(tile);
        }

        // correct guess
        if (tile != null && tile.contains(point)) {
            return Optional.of(tile);
        }

        DTile left = tilemap.get(new Vector2(tileX, tileZ - 1));
        if (left != null && left.contains(point)) {
            return Optional.of(left);
        }

        DTile right = tilemap.get(new Vector2(tileX + 1, tileZ - 1));
        if (right != null && right.contains(point)) {
            return Optional.of(right);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Tile> tileAt(@NotNull Point3 point) {
        return tileAt(new Vector2(point.x(), point.z()));
    }

    @Override
    public Optional<Tile> findTile(@NotNull UUID id) {
        return tiles.stream()
                .filter(tile -> tile.id().equals(id))
                .findFirst().map(tile -> tile);
    }

    @Override
    public Collection<Tile> tiles() {
        return Collections.unmodifiableList(tiles);
    }

    //

    Collection<Tile> adjacent(@NotNull DTile tile) {
        Vector2 vec = new Vector2(tile.position().x(), tile.position().y());
        Set<Tile> tiles = new HashSet<>();
        tiles.add(tilemap.get(vec.add(0, 1)));
        tiles.add(tilemap.get(vec.add(1, 1)));
        tiles.add(tilemap.get(vec.add(0, -1)));
        tiles.add(tilemap.get(vec.add(1, -1)));
        tiles.add(tilemap.get(vec.add(-1, 0)));
        tiles.add(tilemap.get(vec.add(1, 0)));
        tiles.remove(null);
        return tiles;
    }

}
