package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.Tile;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class DHexagonTileRegion extends DTileRegion {

    private transient int tilewidth;

    public DHexagonTileRegion() {
        super();
    }

    public DHexagonTileRegion(UUID worldId, String name, Shape3 shape, int tileRadius) {
        super(worldId, name, shape, tileRadius);
        this.tilewidth = (int) Math.round((tileradius / 2d) * Math.sqrt(3) * 2);
    }

    protected void generate(Shape3 shape) {

        Rectangle bounds = shape.bounds().contour();

        int minTileX = (int) Math.ceil((bounds.min().x() + offset.x()) / (double) tilewidth);
        int maxTileX = (int) Math.floor((bounds.max().x() + offset.x()) / (double) tilewidth);
        int minTileZ = (int) Math.ceil((bounds.min().y() + offset.y()) / (tileradius / 2d * 3));
        int maxTileZ = (int) Math.floor((bounds.max().y() + offset.y()) / (tileradius / 2d * 3));

        int tileRadiusOffset = (int) (tileradius / 2d * 3);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileZ = minTileZ; tileZ <= maxTileZ; tileZ++) {
                double x = offset.x() + tileX * tilewidth;
                double z = offset.y() + tileZ * tileRadiusOffset;
                if (tileZ % 2 != 0) {
                    x += tilewidth / 2d;
                }

                if (x - (tilewidth / 2d) < bounds.min().x()
                        || x + (tilewidth / 2d) > bounds.max().x()
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
        vertices.add(new Vector2(x, z + tileradius));
        vertices.add(new Vector2(x + (tilewidth / 2d), z + (tileradius / 2d)));
        vertices.add(new Vector2(x + (tilewidth / 2d), z - (tileradius / 2d)));
        vertices.add(new Vector2(x, z - tileradius));
        vertices.add(new Vector2(x - (tilewidth / 2d), z - (tileradius / 2d)));
        vertices.add(new Vector2(x - (tilewidth / 2d), z + (tileradius / 2d)));
        return new Polygon(vertices);
    }

    @Override
    public Optional<Tile> tileAt(@NotNull Point2 point) {
        int tileZ = (int) Math.round((offset.y() + point.y()) / (int) (tileradius / 2d * 3));
        double x = point.x();
        if (tileZ % 2 != 0) {
            x -= tilewidth / 2d;
        }
        int tileX = (int) Math.round((offset.x() + x) / tilewidth);

        DTile tile = tilemap.get(new Vector2(tileX, tileZ));

        // is in center rectangle of tile
        if (point.y() < tileZ * (tileradius / 2d) || point.y() > tileZ * tileradius) {
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

    protected Collection<Tile> adjacent(@NotNull DTile tile) {
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
