package com.guflimc.brick.regions.common.engine.zone;

import com.guflimc.brick.maths.api.geo.pos.Point;

import java.util.UUID;

public class CircularRegionZoneContainer extends RegionZoneContainer {

    private final Point origin;
    private final int radiusSquared;

    public CircularRegionZoneContainer(UUID worldId, Point origin, int zoneRadius) {
        super(worldId);
        this.origin = origin;
        this.radiusSquared = zoneRadius * zoneRadius;
    }

    protected RegionZone zoneAt(Point point) {
        double xDiff = point.x() - origin.x();
        double zDiff = point.z() - origin.z();
        double distanceSquared = xDiff * xDiff + zDiff * zDiff;
        int index = (int) (distanceSquared / radiusSquared);

        if (index >= zones.size()) {
            int diff = index - zones.size();
            for (int i = 0; i < diff; i++) {
                zones.add(null);
            }

            zones.add(new RegionZone());
        }

        return zones.get(index);
    }

}
