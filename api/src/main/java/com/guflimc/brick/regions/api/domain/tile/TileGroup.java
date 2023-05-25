package com.guflimc.brick.regions.api.domain.tile;

import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableProtectedLocality;

import java.util.Collection;

public interface TileGroup extends ModifiableLocality, ModifiableProtectedLocality, ModifiableAttributedLocality {

    Collection<Tile> tiles();

    TileRegion parent();

    Shape2 shape();
}