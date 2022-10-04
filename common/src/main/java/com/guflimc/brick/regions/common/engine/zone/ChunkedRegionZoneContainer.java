package com.guflimc.brick.regions.common.engine.zone;

import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.Region;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkedRegionZoneContainer extends RegionZoneContainer {

    private final int chunkSize;
    private final Map<Integer, Map<Integer, RegionZone>> regions = new ConcurrentHashMap<>();

    public ChunkedRegionZoneContainer(UUID worldId, int chunkSize) {
        super(worldId);
        this.chunkSize = chunkSize;
    }

    @Override
    public void remove(Region region) {
        if ( region instanceof AreaRegion ar ) {
            regions.values().forEach(zMap -> zMap.values().forEach(rz -> rz.remove(ar)));
        }
    }

    protected RegionZone zoneAt(Point point) {
        int cx = (int) point.x() / chunkSize;
        int cz = (int) point.z() / chunkSize;

        Map<Integer, RegionZone> zMap = regions.get(cx);
        if ( zMap == null ) {
            zMap = new ConcurrentHashMap<>();
            regions.put(cx, zMap);
        }

        RegionZone rz = zMap.get(cz);
        if ( rz == null ) {
            rz = new RegionZone();
            zMap.put(cz, rz);
        }

        return rz;
    }

}
