package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RegionEngine {

    private final Map<UUID, Region> byId = new ConcurrentHashMap<>();
    private final Map<UUID, RegionContainer> containers = new ConcurrentHashMap<>();

    public RegionContainer addContainer(UUID worldId) {
        return containers.put(worldId, new RegionContainer(worldId));
    }

    public void removeContainer(UUID worldId) {
        containers.remove(worldId);
        byId.keySet().removeIf(id -> byId.get(id).worldId().equals(worldId));
    }

    //

    public void remove(Region region) {
        byId.remove(region.id());
        containers.get(region.worldId()).remove(region);
    }

    public void add(Region region) {
        if ( !containers.containsKey(region.worldId()) ) {
            throw new IllegalStateException("A region container for this world does not exist.");
        }

        byId.put(region.id(), region);
        containers.get(region.worldId()).add(region);
    }

    //

    public Collection<Region> regions() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public Optional<Region> findRegion(@NotNull UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<Region> findRegion(@NotNull UUID worldId, @NotNull String name) {
        return Optional.ofNullable(containers.get(worldId))
                .map(c -> c.findRegion(name));
    }

    public Collection<Region> regionsAt(@NotNull Location point) {
        if (point.worldId() == null) {
            return Collections.emptySet();
        }
        RegionContainer rc = containers.get(point.worldId());
        if (rc == null) {
            return Collections.emptySet();
        }
        return rc.regionsAt(point);
    }

}
