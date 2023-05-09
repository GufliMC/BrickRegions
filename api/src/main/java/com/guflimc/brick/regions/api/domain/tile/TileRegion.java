package com.guflimc.brick.regions.api.domain.tile;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableProtectedLocality;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface TileRegion extends Region, ModifiableLocality, ModifiableProtectedLocality, ModifiableAttributedLocality {

    Collection<TileGroup> groups();

    Optional<TileGroup> groupAt(int relX, int relZ);

    Optional<TileGroup> groupAt(@NotNull Point3 point);

    Collection<TileGroup> intersecting(@NotNull Shape2 shape);

    Collection<TileGroup> adjacent(@NotNull TileGroup group);

    void addGroup(int relX, int relZ);

    void addGroups(Shape2 shape);

    void removeGroup(@NotNull TileGroup group);

    void merge(int maxGroupSize);

    void merge(TileGroup... groups);

    void unmerge(TileGroup group);

}
