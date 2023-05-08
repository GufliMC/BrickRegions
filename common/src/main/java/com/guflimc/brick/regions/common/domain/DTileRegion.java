package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.TileRegion;
import io.ebean.annotation.DbDefault;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Inheritance
public abstract class DTileRegion extends DRegion implements TileRegion {

    @OneToMany(targetEntity = DTile.class, mappedBy = "parent",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected List<DTile> tiles = new ArrayList<>();

    @Column(name = "tileregion_tile_radius")
    @DbDefault("0")
    protected int tileradius;

    @Column(name = "tileregion_offset")
    @Convert(converter = Point2Converter.class)
    protected Point2 offset = new Vector2(0, 0);

    protected transient final Map<Vector2, DTile> tilemap = new HashMap<>();

    public DTileRegion() {
        super();
    }

    public DTileRegion(UUID worldId, String name, Shape3 shape, int tileradius) {
        super(worldId, name);
        this.tileradius = tileradius;

        generate(shape);
        maptiles();

        setAttribute(LocalityAttributeKey.MAP_STROKE_WEIGHT, 1);
    }

    protected abstract void generate(Shape3 shape);

    //

    @PostLoad
    public void maptiles() {
        tilemap.clear();
        tiles.forEach(tile -> tilemap.put((Vector2) tile.position(), tile));
    }

    //

    @Override
    public boolean contains(Point3 point) {
        return tileAt(point).isPresent();
    }

    @Override
    public Optional<Tile> tileAt(@NotNull Point3 point) {
        return tileAt(new Vector2(point.x(), point.z()));
    }

    @Override
    public Optional<Tile> findTile(@NotNull UUID id) {
        return tiles.stream()
                .filter(tile -> tile.id().equals(id))
                .findFirst().map(tile -> tile);
    }

    @Override
    public Collection<Tile> tiles() {
        return Collections.unmodifiableList(tiles);
    }

    @Override
    public Collection<Tile> intersecting(@NotNull Shape2 shape) {
        return tiles.stream()
                .filter(tile -> tile.shape().intersects(shape))
                .collect(Collectors.toList());
    }

    //

    protected abstract Collection<Tile> adjacent(@NotNull DTile tile);

}
