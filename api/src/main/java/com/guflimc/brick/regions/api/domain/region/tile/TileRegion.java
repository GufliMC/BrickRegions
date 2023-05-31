package com.guflimc.brick.regions.api.domain.region.tile;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.regions.api.domain.region.ModifiableRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface TileRegion extends ModifiableRegion {

    Collection<TileGroup> groups();

    Optional<TileGroup> groupAt(@NotNull TileKey key);

    Optional<TileGroup> groupAt(@NotNull Point3 point);

    Shape2 tileShapeAt(@NotNull TileKey key);

    //

    Collection<TileGroup> intersecting(@NotNull Shape2 shape);

    default Collection<TileGroup> intersecting(@NotNull Shape3 shape) {
        return intersecting(shape.contour());
    }

    Collection<TileGroup> adjacent(@NotNull TileGroup group);

    // ADD / REMOVE

    void addGroup(@NotNull TileKey key);

    void addGroups(@NotNull Shape2 shape);

    default void addGroups(@NotNull Shape3 shape) {
        addGroups(shape.contour());
    }

    void removeGroup(@NotNull TileGroup group);

    // MERGE / UNMERGE

    void groupify(int maxGroupSize);

    TileGroup merge(@NotNull TileGroup... groups);

    TileGroup[] unmerge(@NotNull TileGroup group);

}
