package com.guflimc.brick.regions.api.domain.region.tile;

import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.locality.ModifiableLocality;

import java.util.Collection;

public interface TileGroup extends ModifiableLocality {

    Collection<TileKey> tiles();

    TileRegion parent();

    Shape2 shape();
}
