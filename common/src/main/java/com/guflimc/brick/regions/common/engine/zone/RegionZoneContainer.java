package com.guflimc.brick.regions.common.engine.zone;

import com.guflimc.brick.maths.api.geo.area.Contour;
import com.guflimc.brick.maths.api.geo.pos.Point;
import com.guflimc.brick.maths.api.geo.pos.Vector;
import com.guflimc.brick.maths.api.geo.pos.Vector2;
import com.guflimc.brick.regions.api.domain.AreaRegion;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.common.engine.RegionContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class RegionZoneContainer extends RegionContainer {

    public RegionZoneContainer(UUID worldId) {
        super(worldId);
    }

    @Override
    public void add(Region region) {
        if (region instanceof AreaRegion ar) {
            Contour contour = ar.area().contour();
            for (Vector2 vec : contour) {
                zoneAt(new Vector(vec.x(), 0, vec.y())).add(ar);
            }
            // TODO | what if a side of the contour is greater than the chunk length?
            // TODO | or passes diagonally by a corner of a chunk
        }
        // TODO else
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Point point) {
        return zoneAt(point).regionsAt(point);
    }

    protected abstract RegionZone zoneAt(Point point);

}
