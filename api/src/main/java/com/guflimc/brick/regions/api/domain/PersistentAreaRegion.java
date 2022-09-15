package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.maths.api.geo.area.Area;

public interface PersistentAreaRegion extends PersistentRegion, AreaRegion{

    void setArea(Area area);

}
