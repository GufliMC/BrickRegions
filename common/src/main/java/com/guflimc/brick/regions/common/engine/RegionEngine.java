package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.math.common.geometry.pos3.Location;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegionEngine {

    private final Map<UUID, Region.Keyed> byId = new ConcurrentHashMap<>();
    private final Map<UUID, RegionContainer> containers = new ConcurrentHashMap<>();

    public <T extends Region.World & Region.Keyed> void addContainer(@NotNull T region) {
        containers.put(region.worldId(), new RegionContainer(region.worldId(), region));
    }

    public void removeContainer(@NotNull UUID worldId) {
        containers.remove(worldId);
        byId.entrySet().removeIf(e -> e.getValue().worldId().equals(worldId));
    }

    //

    public void addRegion(@NotNull Region region) {
        if (!containers.containsKey(region.worldId())) {
            throw new IllegalStateException("A region container for the given world does not exist.");
        }

        containers.get(region.worldId()).addRegion(region);

        if ( region instanceof Region.Keyed rk )
            byId.put(rk.id(), rk);
    }

    public void removeRegion(@NotNull Region region) {
        if (containers.get(region.worldId()).world().equals(region)) {
            return;
        }

        containers.get(region.worldId()).removeRegion(region);

        if ( region instanceof Region.Keyed rk )
            byId.remove(rk.id(), rk);
    }

    //

    public Region.World world(@NotNull UUID worldId) {
        if (!containers.containsKey(worldId)) {
            throw new IllegalStateException("A region container for the given world does not exist.");
        }
        return containers.get(worldId).world();
    }

    public Collection<Region> regions() {
        return containers.values().stream()
                .flatMap(rc -> rc.regions().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Collection<Region> regions(@NotNull UUID worldId) {
        if (!containers.containsKey(worldId)) {
            throw new IllegalStateException("A region container for the given world does not exist.");
        }
        return containers.get(worldId).regions();
    }

    public Optional<Region.Keyed> region(@NotNull UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<Region.Keyed> region(@NotNull UUID worldId, @NotNull String name) {
        if (!containers.containsKey(worldId)) {
            throw new IllegalStateException("A region container for the given world does not exist.");
        }
        return containers.get(worldId).region(name);
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
