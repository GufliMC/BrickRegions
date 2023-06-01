package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.tile.TileKey;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class DTileGroup extends DRegion implements TileGroup {

    @JoinColumn(name = "tilegroup_tileregion_id")
    @ManyToOne(targetEntity = DTileRegion.class, fetch = FetchType.EAGER, optional = false)
    private DTileRegion region;

    @Column(name = "tilegroup_tiles")
    @Convert(converter = TileKeySetConverter.class)
    private TileKeySet tileSet = new TileKeySet(new ArrayList<>());

    private transient Shape2 shape;

    public DTileGroup() {
    }

    DTileGroup(DTileRegion region, TileKey... tiles) {
        super(region.worldId());
        this.region = region;
        this.tileSet = new TileKeySet(Arrays.asList(tiles));
        calcshape();
    }

    @PostLoad
    public void calcshape() {
        this.shape = Polygon.merge(this.tileSet.tiles.stream().map(tile -> region.tileShapeAt(tile)).toArray(Shape2[]::new));
    }

    @Override
    public Collection<TileKey> tiles() {
        return Collections.unmodifiableList(this.tileSet.tiles);
    }

    @Override
    public TileRegion parent() {
        return region;
    }

    @Override
    public boolean contains(@NotNull Point3 point) {
        Point2 p2 = new Vector2(point.x(), point.z());
        return this.tileSet.tiles.stream().anyMatch(tile -> region.tileShapeAt(tile).contains(p2));
    }

    @Override
    public <U> Optional<U> attribute(RegionAttributeKey<U> key) {
        Optional<U> opt = super.attribute(key);
        if (opt.isPresent()) {
            return opt;
        }
        return region.attribute(key);
    }

    @Override
    public Shape2 shape() {
        return shape;
    }

    @Override
    public int priority() {
        // current priority + parent priority
        return super.priority() + region.priority();
    }

    @Override
    public String toString() {
        return "TileGroup{" + this.tileSet.tiles.stream().map(TileKey::toString).collect(Collectors.joining(", ")) + "}";
    }

    //

    public static record TileKeySet(List<TileKey> tiles) {
    }

    //

    public static class TileKeySetConverter implements AttributeConverter<TileKeySet, String> {

        @Override
        public String convertToDatabaseColumn(TileKeySet attribute) {
            return attribute.tiles.stream().map(tile -> tile.x() + "," + tile.z()).collect(Collectors.joining(";"));
        }

        @Override
        public TileKeySet convertToEntityAttribute(String dbData) {
            String[] tiles = dbData.split(";");
            return new TileKeySet(Arrays.stream(tiles).map(tile -> {
                String[] coords = tile.split(",");
                return new TileKey(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            }).collect(Collectors.toList()));
        }
    }

}
