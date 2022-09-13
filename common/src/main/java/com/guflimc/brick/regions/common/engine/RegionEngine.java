package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.pos.Location;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RegionEngine {

    private final Map<UUID, Region> byId = new ConcurrentHashMap<>();
    private final Map<String, Region> byName = new ConcurrentHashMap<>();

    private final Map<UUID, RegionContainer> worldEngines = new ConcurrentHashMap<>();
    private final Function<UUID, RegionContainer> factory;

    public RegionEngine(Function<UUID, RegionContainer> factory) {
        this.factory = factory;
    }

    public void remove(Region region) {
        byId.remove(region.id());
        byName.remove(region.name());

        RegionContainer we = worldEngines.get(region.worldId());
        if (we != null) {
            we.remove(region);
        }
    }

    public void add(Region region) {
        byId.put(region.id(), region);
        byName.put(region.name(), region);

        RegionContainer we = worldEngines.get(region.worldId());
        if (we == null) {
            we = factory.apply(region.worldId());
            worldEngines.put(we.worldId(), we);
        }

        we.add(region);
    }

    public Optional<Region> findRegion(@NotNull UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<Region> findRegion(@NotNull String name) {
        return Optional.ofNullable(byName.get(name));
    }

    public Collection<Region> regions() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public Collection<Region> regionsAt(@NotNull Location point) {
        if (point.worldId() == null) {
            return Collections.emptySet();
        }
        RegionContainer we = worldEngines.get(point.worldId());
        if (we == null) {
            return Collections.emptySet();
        }
        return we.regionsAt(point);
    }

}
