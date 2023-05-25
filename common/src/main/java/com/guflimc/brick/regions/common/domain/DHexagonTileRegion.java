package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape2d.RegularHexagon;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.tile.Tile;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.common.EventManager;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class DHexagonTileRegion extends DTileRegion {

    public DHexagonTileRegion() {
        super();
    }

    public DHexagonTileRegion(UUID worldId, String name, int radius) {
        super(worldId, name, radius);
    }

    @Override
    public Optional<TileGroup> groupAt(@NotNull Point2 point) {
        double width = width();
        double heightOffset = heightOffset();

        int tileZ = (int) Math.round((offset.y() + point.y()) / (int) heightOffset);
        double x = point.x();
        if (tileZ % 2 != 0) {
            x -= width / 2d;
        }
        int tileX = (int) Math.round((offset.x() + x) / width);

        DTileGroup group = tilemap.get(new Vector2(tileX, tileZ));

        // is in center rectangle of tile
        if (point.y() < tileZ * (radius / 2d) || point.y() > tileZ * radius) {
            return Optional.ofNullable(group);
        }

        // correct guess
        if (group != null && group.shape().contains(point)) {
            return Optional.of(group);
        }

        TileGroup left = tilemap.get(new Vector2(tileX, tileZ - 1));
        if (left != null && left.shape().contains(point)) {
            return Optional.of(left);
        }

        TileGroup right = tilemap.get(new Vector2(tileX + 1, tileZ - 1));
        if (right != null && right.shape().contains(point)) {
            return Optional.of(right);
        }

        return Optional.empty();
    }

    @Override
    public Collection<TileGroup> adjacent(@NotNull TileGroup group) {
        Set<TileGroup> result = new HashSet<>();
        for (Tile tile : group.tiles()) {
            Vector2 vec = new Vector2(tile.position().x(), tile.position().y());
            int offset = vec.blockY() % 2 == 0 ? -1 : 1;
            groupAt(vec.blockX() + offset, vec.blockY() - 1).ifPresent(result::add);
            groupAt(vec.blockX() + offset, vec.blockY()).ifPresent(result::add);
            groupAt(vec.blockX() + offset, vec.blockY() + 1).ifPresent(result::add);

            groupAt(vec.blockX(), vec.blockY() - 1).ifPresent(result::add);
            groupAt(vec.blockX() - offset, vec.blockY()).ifPresent(result::add);
            groupAt(vec.blockX(), vec.blockY() + 1).ifPresent(result::add);
        }
        result.remove(group);
        return result;
    }

    private record TempTile(Point2 position, Shape2 shape) implements Tile {
    }

    private void addGroup(int relX, int relZ, RegularHexagon hexagon) {
        groups.add(new DTileGroup(this, new TempTile(new Vector2(relX, relZ), hexagon)));
        EventManager.INSTANCE.onPropertyChange(this);
    }

    @Override
    public void addGroup(int relX, int relZ) {
        if (groupAt(relX, relZ).isPresent()) {
            throw new IllegalArgumentException("A group at this position already exists.");
        }

        addGroup(relX, relZ, hexagonAt(relX, relZ));
    }

    @Override
    public void addGroups(Shape2 shape) {
        Rectangle bounds = shape.bounds();

        double width = width();
        double heightOffset = heightOffset();

        int minTileX = (int) Math.ceil((bounds.min().x() + offset.x()) / width);
        int maxTileX = (int) Math.floor((bounds.max().x() + offset.x()) / width);
        int minTileZ = (int) Math.ceil((bounds.min().y() + offset.y()) / heightOffset);
        int maxTileZ = (int) Math.floor((bounds.max().y() + offset.y()) / heightOffset);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileZ = minTileZ; tileZ <= maxTileZ; tileZ++) {
                if (groupAt(tileX, tileZ).isPresent()) {
                    continue;
                }

                RegularHexagon hexagon = hexagonAt(tileX, tileZ);
                if (!hexagon.vertices().stream().allMatch(shape::contains)) {
                    continue;
                }

                addGroup(tileX, tileZ, hexagon);
            }
        }
    }

    //

    private double width() {
        return 1.73205080757 * radius;
    }

    private double heightOffset() {
        return radius / 2d * 3d;
    }

    private RegularHexagon hexagonAt(int relX, int relZ) {
        double width = width();
        double heightOffset = heightOffset();

        double x = offset.x() + relX * width;
        double z = offset.y() + relZ * heightOffset;
        if (relZ % 2 != 0) {
            x += width / 2d;
        }

        return new RegularHexagon(new Vector2(x, z), radius);
    }

}
