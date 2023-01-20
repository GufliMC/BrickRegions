package com.guflimc.brick.regions.api.domain.modifiable;

import com.guflimc.brick.regions.api.domain.Locality;

public interface ModifiableLocality extends Locality {

    void setPriority(int priority);

}

