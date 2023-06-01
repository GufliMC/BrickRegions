package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Rectangle;
import com.guflimc.brick.math.common.geometry.shape2d.RegularHexagon;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.tile.TileKey;
import com.guflimc.brick.regions.common.EventManager;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class DHexagonTileRegion extends DTileRegion {

    public DHexagonTileRegion() {
        super();
    }

    public DHexagonTileRegion(@NotNull UUID worldId, @NotNull String name, int radius) {
        super(worldId, name, radius);
    }

    @Override
    public Optional<TileGroup> groupAt(@NotNull Point3 point) {
        double width = width();
        double heightOffset = heightOffset();

        int tileZ = (int) Math.round((offset.y() + point.z()) / (int) heightOffset);
        double x = point.x();
        if (tileZ % 2 != 0) {
            x -= width / 2d;
        }
        int tileX = (int) Math.round((offset.x() + x) / width);

        DTileGroup group = tilemap.get(new TileKey(tileX, tileZ));

        // is in center rectangle of tile
        if (point.z() < tileZ * (radius / 2d) || point.z() > tileZ * radius) {
            return Optional.ofNullable(group);
        }

        Point2 p2d = new Vector2(point.x(), point.z());

        // correct guess
        if (group != null && group.shape().contains(p2d)) {
            return Optional.of(group);
        }

        TileGroup left = tilemap.get(new TileKey(tileX, tileZ - 1));
        if (left != null && left.shape().contains(p2d)) {
            return Optional.of(left);
        }

        TileGroup right = tilemap.get(new TileKey(tileX + 1, tileZ - 1));
        if (right != null && right.shape().contains(p2d)) {
            return Optional.of(right);
        }

        return Optional.empty();
    }

    @Override
    public Collection<TileGroup> adjacent(@NotNull TileGroup group) {
        Set<TileGroup> result = new HashSet<>();
        for (TileKey tile : group.tiles()) {
            Vector2 vec = new Vector2(tile.x(), tile.z());
            int offset = vec.blockY() % 2 == 0 ? -1 : 1;
            groupAt(new TileKey(vec.blockX() + offset, vec.blockY() - 1)).ifPresent(result::add);
            groupAt(new TileKey(vec.blockX() + offset, vec.blockY())).ifPresent(result::add);
            groupAt(new TileKey(vec.blockX() + offset, vec.blockY() + 1)).ifPresent(result::add);

            groupAt(new TileKey(vec.blockX(), vec.blockY() - 1)).ifPresent(result::add);
            groupAt(new TileKey(vec.blockX() - offset, vec.blockY())).ifPresent(result::add);
            groupAt(new TileKey(vec.blockX(), vec.blockY() + 1)).ifPresent(result::add);
        }
        result.remove(group);
        return result;
    }

    @Override
    public void addGroup(@NotNull TileKey key) {
        if (groupAt(key).isPresent()) {
            throw new IllegalArgumentException("A group at this position already exists.");
        }

        groups.add(new DTileGroup(this, key));
        EventManager.INSTANCE.onPropertyChange(this);
    }

    @Override
    public void addGroups(@NotNull Shape2 shape) {
        Rectangle bounds = shape.bounds();

        double width = width();
        double heightOffset = heightOffset();

        int minTileX = (int) Math.ceil((bounds.min().x() + offset.x()) / width);
        int maxTileX = (int) Math.floor((bounds.max().x() + offset.x()) / width);
        int minTileZ = (int) Math.ceil((bounds.min().y() + offset.y()) / heightOffset);
        int maxTileZ = (int) Math.floor((bounds.max().y() + offset.y()) / heightOffset);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileZ = minTileZ; tileZ <= maxTileZ; tileZ++) {
                TileKey key = new TileKey(tileX, tileZ);
                if (groupAt(key).isPresent()) {
                    continue;
                }

                RegularHexagon hexagon = tileShapeAt(key);
                if (!hexagon.vertices().stream().allMatch(shape::contains)) {
                    continue;
                }

                addGroup(key);
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

    @Override
    public RegularHexagon tileShapeAt(@NotNull TileKey key) {
        double width = width();
        double heightOffset = heightOffset();

        double x = offset.x() + key.x() * width;
        double z = offset.y() + key.z() * heightOffset;
        if (key.z() % 2 != 0) {
            x += width / 2d;
        }

        return new RegularHexagon(new Vector2(x, z), radius);
    }

}
