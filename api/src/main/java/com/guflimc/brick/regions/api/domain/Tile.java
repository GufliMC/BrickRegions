package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableProtectedLocality;

public interface Tile extends ModifiableLocality, ModifiableProtectedLocality, ModifiableAttributedLocality {

    Point2 position();

    Shape2 shape();

}
