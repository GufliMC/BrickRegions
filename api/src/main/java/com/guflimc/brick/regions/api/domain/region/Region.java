package com.guflimc.brick.regions.api.domain.region;

import com.guflimc.brick.regions.api.domain.locality.Locality;

public interface Region extends Locality {

    RegionKey key();

    boolean archived();

}
