package com.guflimc.brick.regions.api.domain.region;

import com.guflimc.brick.regions.api.domain.locality.ModifiableLocality;

public interface ModifiableRegion extends Region, ModifiableLocality {

    void setArchived(boolean archived);

}
