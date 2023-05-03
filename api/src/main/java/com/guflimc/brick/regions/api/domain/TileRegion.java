package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableProtectedLocality;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TileRegion extends Region, ModifiableLocality, ModifiableProtectedLocality, ModifiableAttributedLocality {

    Optional<Tile> tileAt(@NotNull Point3 point);

    Optional<Tile> tileAt(@NotNull Point2 point);

    Optional<Tile> findTile(@NotNull UUID id);

    Collection<Tile> tiles();

    Collection<Tile> intersecting(@NotNull Shape2 shape);

}
