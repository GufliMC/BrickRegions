package com.guflimc.brick.regions.api.selection;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.api.geo.area.Contour;

public interface Selection {

    boolean isValid();

    Contour contour();

    double minY();

    double maxY();

    void expandY();

    void undo();

    Area area();

}
