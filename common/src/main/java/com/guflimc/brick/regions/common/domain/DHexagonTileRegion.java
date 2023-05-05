package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.Tile;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class DHexagonTileRegion extends DTileRegion {


    public DHexagonTileRegion() {
        super();
    }

    public DHexagonTileRegion(UUID worldId, String name, Shape3 shape, int tileRadius) {
        super(worldId, name, shape, tileRadius);
    }

    protected void generate(Shape3 shape) {
        Shape2 contour = shape.contour();
        Rectangle bounds = shape.bounds().contour();

        double width = width(tileradius);
        double heightOffset = (int) (tileradius / 2d * 3);

        int minTileX = (int) Math.ceil((bounds.min().x() + offset.x()) / width);
        int maxTileX = (int) Math.floor((bounds.max().x() + offset.x()) / width);
        int minTileZ = (int) Math.ceil((bounds.min().y() + offset.y()) / heightOffset);
        int maxTileZ = (int) Math.floor((bounds.max().y() + offset.y()) / heightOffset);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileZ = minTileZ; tileZ <= maxTileZ; tileZ++) {
                double x = offset.x() + tileX * width;
                double z = offset.y() + tileZ * heightOffset;
                if (tileZ % 2 != 0) {
                    x += width / 2d;
                }

                if (x - (width / 2d) < bounds.min().x()
                        || x + (width / 2d) > bounds.max().x()
                        || z - heightOffset < bounds.min().y()
                        || z + heightOffset > bounds.max().y()) {
                    continue;
                }

                Hexagon hexagon = new Hexagon(new Vector2(x, z), tileradius);
                if ( !hexagon.vertices().stream().allMatch(contour::contains) ) {
                    continue;
                }

                tiles.add(new DTile(this, new Vector2(tileX, tileZ), hexagon));
            }
        }
    }

    @Override
    public Optional<Tile> tileAt(@NotNull Point2 point) {
        double width = width(tileradius);

        int tileZ = (int) Math.round((offset.y() + point.y()) / (int) (tileradius / 2d * 3));
        double x = point.x();
        if (tileZ % 2 != 0) {
            x -= width / 2d;
        }
        int tileX = (int) Math.round((offset.x() + x) / width);

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

    //

    private static double width(int radius) {
        return (radius * 2d) / 1.73205080757d;
    }

    private record Hexagon(Point2 center, int radius) implements Shape2 {

        private double width() {
            return DHexagonTileRegion.width(radius);
        }

        @Override
        public List<Vector2> vertices() {
            double w = width();
            return List.of(
                    new Vector2(center.x(), center.y() + radius),
                    new Vector2(center.x() + w, center.y() + (radius / 2d)),
                    new Vector2(center.x() + w, center.y() - (radius / 2d)),
                    new Vector2(center.x(), center.y() - radius),
                    new Vector2(center.x() - w, center.y() - (radius / 2d)),
                    new Vector2(center.x() - w, center.y() + (radius / 2d))
            );
        }

        @Override
        public boolean contains(Point2 point) {
            double qx = Math.abs(point.x() - center.x());
            double qy = Math.abs(point.y() - center.y());
            if ( qx > radius || qy > radius ) return false; // outside circle
            if ( qx < radius && qy < radius / 2d ) return true; // inside rectangular part
            double v = (radius / 2d) - 0.660254 * qx; // inside triangle part
            return qy <= v;
        }

        @Override
        public Rectangle bounds() {
            return new Rectangle(
                    new Vector2(center.x() - radius, center.y() - radius),
                    new Vector2(center.x() + radius, center.y() + radius)
            );
        }

        @NotNull
        @Override
        public Iterator<Point2> iterator() {
            throw new UnsupportedOperationException("Not implemented yet"); // TODO;
        }
    }
}
