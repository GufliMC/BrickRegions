package com.guflimc.brick.regions.api.domain.tile;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;

public interface Tile {

    Point2 position();

    Shape2 shape();


}
