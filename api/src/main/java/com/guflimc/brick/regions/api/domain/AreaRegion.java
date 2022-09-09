package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.area.Area;

public interface AreaRegion extends Region {

    Area area();

    void setArea(Area area);

}
