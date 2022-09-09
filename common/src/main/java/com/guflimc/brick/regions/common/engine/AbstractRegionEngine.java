package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegionEngine implements RegionEngine {

    private final Map<UUID, Region> byId = new ConcurrentHashMap<>();
    private final Map<String, Region> byName = new ConcurrentHashMap<>();

    @Override
    public void remove(Region region) {
        byId.remove(region.id());
        byName.remove(region.name());
    }

    @Override
    public void add(Region region) {
        byId.put(region.id(), region);
        byName.put(region.name(), region);
    }

    @Override
    public Optional<Region> findRegion(@NotNull UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Region> findRegion(@NotNull String name) {
        return Optional.ofNullable(byName.get(name));
    }

    @Override
    public Collection<Region> regions() {
        return Collections.unmodifiableCollection(byId.values());
    }

}
