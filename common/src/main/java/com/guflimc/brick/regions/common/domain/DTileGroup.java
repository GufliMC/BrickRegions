package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Polygon;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.Tile;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class DTileGroup extends DModifiableLocality implements TileGroup {

    @Column(name = "tile_group_region_id")
    @ManyToOne(targetEntity = DTileRegion.class, fetch = FetchType.EAGER)
    private DTileRegion region;

    @Column(name = "tile_group_tiles")
    @OneToMany(targetEntity = DTile.class, mappedBy = "group",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DTile> tiles = new ArrayList<>();

    private transient Shape2 shape;

    public DTileGroup() {
    }

    DTileGroup(DTileRegion region, Tile... tiles) {
        super(region.worldId());
        this.region = region;
        Arrays.asList(tiles).forEach(tile -> this.tiles.add(new DTile(this, tile.position(), tile.shape())));
        calcshape();
    }

    @PostLoad
    public void calcshape() {
        this.shape = Polygon.merge(this.tiles.stream().map(Tile::shape).toArray(Shape2[]::new));
    }

    @Override
    public Collection<Tile> tiles() {
        return this.tiles.stream().map(tile -> (Tile) tile).toList();
    }

    @Override
    public TileRegion region() {
        return region;
    }

    @Override
    public boolean contains(Point3 point) {
        Point2 p2 = new Vector2(point.x(), point.z());
        return this.tiles.stream().anyMatch(tile -> tile.shape().contains(p2));
    }

    @Override
    public <U> Optional<U> attribute(LocalityAttributeKey<U> key) {
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
    public String toString() {
        return "TileRegion{" + this.tiles.stream().map(Tile::toString).collect(Collectors.joining(", ")) + "}";
    }

}
