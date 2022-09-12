package com.guflimc.brick.regions.common.engine;

import com.guflimc.brick.maths.api.geo.area.Contour;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector;
import com.guflimc.brick.maths.api.geo.pos.Vector2;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class ZonedWorldRegionEngine implements WorldRegionEngine {

    private final ZoneSet zoneSet;
    private final UUID worldId;

    public ZonedWorldRegionEngine(UUID worldId, Point origin, int zoneRadius) {
        this.worldId = worldId;
        this.zoneSet = new ZoneSet(origin, zoneRadius);
    }

    @Override
    public UUID worldId() {
        return worldId;
    }

    @Override
    public void remove(Region region) {
        if (region instanceof AreaRegion ar) {
            zoneSet.remove(ar);
        }
        // TODO else
    }

    @Override
    public void add(Region region) {
        if (region instanceof AreaRegion ar) {
            zoneSet.add(ar);
        }
        // TODO else
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Point point) {
        return zoneSet.regionsAt(point);
    }

    //

    private static class ZoneSet {

        private final List<Zone> zones = new CopyOnWriteArrayList<>();

        private final Point origin;
        private final int radiusSquared;

        private ZoneSet(Point origin, int radius) {
            this.origin = origin;
            this.radiusSquared = radius * radius;
        }

        private Zone zoneAt(Point point) {
            double xDiff = point.x() - origin.x();
            double zDiff = point.z() - origin.z();
            double distanceSquared = xDiff * xDiff + zDiff * zDiff;
            int index = (int) (distanceSquared / radiusSquared);

            if (zones.size() < index) {
                int diff = index - zones.size();
                for (int i = 0; i < diff; i++) {
                    zones.add(null);
                }

                zones.add(new Zone());
            }

            return zones.get(index);
        }

        public Collection<Region> regionsAt(@NotNull Point point) {
            return zoneAt(point).regionsAt(point);
        }

        public void add(AreaRegion region) {
            Contour contour = region.area().contour();
            for (Vector2 vec : contour) {
                zoneAt(new Vector(vec.x(), 0, vec.y())).add(region);
            }
        }

        public void remove(AreaRegion region) {
            zones.forEach(z -> z.remove(region));
        }
    }

    private static class Zone {

        private final Set<AreaRegion> regions = new CopyOnWriteArraySet<>();

        public Collection<Region> regionsAt(@NotNull Point point) {
            return regions.stream()
                    .filter(rg -> rg.area().contains(point))
                    .collect(Collectors.toUnmodifiableSet());
        }

        public void add(AreaRegion region) {
            regions.add(region);
        }

        public void remove(AreaRegion region) {
            regions.remove(region);
        }
    }
}
