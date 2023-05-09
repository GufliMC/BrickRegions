package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableTileRegion;
import com.guflimc.brick.regions.api.domain.tile.Tile;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.common.EventManager;
import io.ebean.annotation.DbDefault;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Inheritance
public abstract class DTileRegion extends DRegion implements ModifiableTileRegion {

    @OneToMany(targetEntity = DTileGroup.class, mappedBy = "region",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected List<DTileGroup> groups = new ArrayList<>();

    @Column(name = "tileregion_tile_radius")
    @DbDefault("0")
    protected int radius;

    @Column(name = "tileregion_offset")
    @Convert(converter = Point2Converter.class)
    protected Point2 offset = new Vector2(0, 0);

    protected transient final Map<Point2, DTileGroup> tilemap = new HashMap<>();

    public DTileRegion() {
        super();
    }

    public DTileRegion(UUID worldId, String name, int radius) {
        super(worldId, name);
        this.radius = radius;

        setAttribute(LocalityAttributeKey.MAP_STROKE_WEIGHT, 1);
    }

    //

    @PostLoad
    public void maptiles() {
        for ( DTileGroup group : groups ) {
            group.tiles().forEach(tile -> tilemap.put(tile.position(), group));
        }
    }

    //

    @Override
    public boolean contains(Point3 point) {
        return groupAt(point).isPresent();
    }

    @Override
    public Optional<TileGroup> groupAt(int relX, int relZ) {
        return Optional.ofNullable(tilemap.get(new Vector2(relX, relZ)));
    }

    @Override
    public Optional<TileGroup> groupAt(@NotNull Point3 point) {
        return groupAt(new Vector2(point.x(), point.z()));
    }

    public abstract Optional<TileGroup> groupAt(@NotNull Point2 point);

    @Override
    public Collection<TileGroup> groups() {
        return Collections.unmodifiableCollection(groups);
    }

    @Override
    public Collection<TileGroup> intersecting(@NotNull Shape2 shape) {
        return tilemap.values().stream()
                .filter(group -> group.shape().intersects(shape))
                .collect(Collectors.toList());
    }

    @Override
    public void removeGroup(@NotNull TileGroup group) {
        groups.remove((DTileGroup) group);
        EventManager.INSTANCE.onTileRegionChange(this);
    }

    @Override
    public void merge(TileGroup... groups) {
        // TODO check adjacency

        Set<Tile> tiles = Arrays.stream(groups)
                .flatMap(g -> g.tiles().stream())
                .collect(Collectors.toSet());

        Arrays.stream(groups).map(g -> (DTileGroup) g).forEach(g -> this.groups.remove(g));
        this.groups.add(new DTileGroup(this, tiles.toArray(Tile[]::new)));

        EventManager.INSTANCE.onTileRegionChange(this);
    }

    @Override
    public void unmerge(TileGroup group) {
        this.groups.remove((DTileGroup) group);

        for ( Tile tile : group.tiles() ) {
            this.groups.add(new DTileGroup(this, tile));
        }

        EventManager.INSTANCE.onTileRegionChange(this);
    }

    //

    private final static Random random = new Random();

    @Override
    public void merge(int size) {
        new HashSet<>(groups).forEach(this::unmerge);

        Map<Point2, Tile> tiles = new HashMap<>();
        groups.stream().flatMap(g -> g.tiles().stream()).forEach(t -> tiles.put(t.position(), t));

        Set<TileGroup> frontier = new HashSet<>();

        int radius = 0;
        while ( !tiles.isEmpty() ) {
            frontier.removeIf(tg -> tg.tiles().size() >= size);

            // get contouring tiles of current radius
            List<Tile> contour = new ArrayList<>();
            for ( int rx = -1; rx <= 1; rx += 2) {
                int x = radius * rx;
                for ( int z = -radius; z <= radius; z++ ) {
                    Tile tile = tiles.get(new Vector2(x, z));
                    if ( tile != null ) contour.add(tile);
                }
            }
            for ( int rz = -1; rz <= 1; rz += 2) {
                int z = radius * rz;
                for ( int x = -radius; x <= radius; x++ ) {
                    Tile tile = tiles.get(new Vector2(x, z));
                    if ( tile != null ) contour.add(tile);
                }
            }

            contour.forEach(t -> tiles.remove(t.position()));
            Collections.shuffle(contour);

            // add new groups to the frontier
            for ( Tile tile : new ArrayList<>(contour) ) {
                TileGroup group = groupAt(tile.position()).orElseThrow();

                // TODO fix this
                List<TileGroup> adjacent = adjacent(group).stream()
                        .filter(frontier::contains)
                        .distinct()
                        .toList();

                if ( adjacent.isEmpty() ) {
                    frontier.add(group);
                    continue;
                }

                TileGroup target = adjacent.stream().min(Comparator.comparingInt(tg -> tg.tiles().size())).orElseThrow();
                merge(target, group);

                if ( target.tiles().size() >= size ) {
                    frontier.remove(target);
                }
            }

            radius++;

            System.out.println("contour: " + contour.size());
            System.out.println("total: " + tiles.size());
        }
    }

}
