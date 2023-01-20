package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;

import java.util.Collection;

public interface TileRegion extends Region {

    Tile tileAt(Point3 point);

    Collection<Tile> tiles();

}
