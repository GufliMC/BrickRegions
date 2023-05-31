package com.guflimc.brick.regions.api.domain.region;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.locality.ModifiableLocality;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface WorldRegion extends Region, ModifiableLocality {

    @Override
    default boolean contains(@NotNull Point3 point) {
        return !(point instanceof Location loc) || Objects.equals(loc.worldId(), worldId());
    }
}
