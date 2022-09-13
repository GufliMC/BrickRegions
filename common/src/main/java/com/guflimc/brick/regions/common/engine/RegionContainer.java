package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public abstract class RegionContainer {

    private final UUID worldId;

    public RegionContainer(UUID worldId) {
        this.worldId = worldId;
    }

    public UUID worldId() {
        return worldId;
    }

    public abstract void remove(Region region);

    public abstract void add(Region region);

    public abstract Collection<Region> regionsAt(@NotNull Point point);

}
