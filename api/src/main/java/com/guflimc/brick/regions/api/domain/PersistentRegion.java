package com.guflimc.brick.regions.api.domain;

import net.kyori.adventure.text.Component;

public interface PersistentRegion extends Region, PersistentProtectedLocality {

    void setDisplayName(Component displayName);

}
