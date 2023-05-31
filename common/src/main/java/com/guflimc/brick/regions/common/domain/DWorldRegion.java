package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.regions.api.domain.region.RegionKey;
import com.guflimc.brick.regions.api.domain.region.WorldRegion;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class DWorldRegion extends DRegion implements WorldRegion {

    public DWorldRegion() {
        super();
    }

    public DWorldRegion(UUID worldId, RegionKey key) {
        super(worldId, key);
        this.setPriority(0);
    }

    @Override
    public void setPriority(int priority) {
        throw new UnsupportedOperationException("WorldRegion priority cannot be changed");
    }

    @Override
    public boolean contains(@NotNull Point3 point) {
        return WorldRegion.super.contains(point);
    }
}
