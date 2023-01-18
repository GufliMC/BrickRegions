package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;

public interface Tile extends PersistentLocality, PersistentProtectedLocality {

    Point2 position();

    Polygon polygon();

}
