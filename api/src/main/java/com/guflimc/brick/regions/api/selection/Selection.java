package com.guflimc.brick.regions.api.selection;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.Contour;

import java.util.UUID;

public interface Selection {

    UUID worldId();

    double minY();

    double maxY();

    boolean isValid();

    void expandY();

    void undo();

    Area area();

}
