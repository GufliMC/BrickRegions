package com.guflimc.brick.regions.api.domain.tile;

import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.Region;

import java.util.Collection;

public interface TileGroup extends Region {

    Collection<TileKey> tiles();

    TileRegion parent();

    Shape2 shape();
}
