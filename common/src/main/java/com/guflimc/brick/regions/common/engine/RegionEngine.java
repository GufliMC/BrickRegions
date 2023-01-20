package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegionEngine {

    private final Map<UUID, Region> byId = new ConcurrentHashMap<>();
    private final Map<UUID, RegionContainer> containers = new ConcurrentHashMap<>();

    public void addContainer(@NotNull UUID worldId, @NotNull WorldRegion worldRegion) {
        containers.put(worldId, new RegionContainer(worldId, worldRegion));
    }

    public void removeContainer(@NotNull UUID worldId) {
        containers.remove(worldId);
        byId.keySet().removeIf(id -> byId.get(id).worldId().equals(worldId));
    }

    //

    public void remove(@NotNull Region region) {
        byId.remove(region.id());
        containers.get(region.worldId()).remove(region);
    }

    public void add(@NotNull Region region) {
        if (!containers.containsKey(region.worldId())) {
            throw new IllegalStateException("A region container for this world does not exist.");
        }

        byId.put(region.id(), region);
        containers.get(region.worldId()).add(region);
    }

    //

    public Collection<Region> regions() {
        return containers.values().stream()
                .flatMap(rc -> rc.regions().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<Region> regions(@NotNull UUID worldId) {
        if (!containers.containsKey(worldId)) {
            throw new IllegalStateException("A region container for this world does not exist.");
        }
        return containers.get(worldId).regions();
    }

    public WorldRegion worldRegion(@NotNull UUID worldId) {
        if (!containers.containsKey(worldId)) {
            throw new IllegalStateException("A region container for this world does not exist.");
        }
        return containers.get(worldId).worldRegion();
    }

    public Optional<Region> findRegion(@NotNull UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<Region> findRegion(@NotNull UUID worldId, @NotNull String name) {
        if (!containers.containsKey(worldId)) {
            throw new IllegalStateException("A region container for this world does not exist.");
        }
        return containers.get(worldId).findRegion(name);
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
