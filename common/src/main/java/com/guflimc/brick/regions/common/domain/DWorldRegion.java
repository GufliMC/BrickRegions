package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class DWorldRegion extends DKeyedRegion implements Region.World {

    public DWorldRegion() {
    }

    public DWorldRegion(@NotNull UUID worldId) {
        super(worldId, "__global__");
    }

    @Override
    public boolean contains(@NotNull Point3 point) {
        return Region.World.super.contains(point);
    }
}
