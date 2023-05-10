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

        group.tiles().forEach(tile -> tilemap.remove(tile.position()));
    }

    @Override
    public TileGroup merge(TileGroup... groups) {
        // TODO check adjacency

        Set<Tile> tiles = Arrays.stream(groups)
                .flatMap(g -> g.tiles().stream())
                .collect(Collectors.toSet());

        Arrays.stream(groups).map(g -> (DTileGroup) g).forEach(g -> this.groups.remove(g));
        DTileGroup group = new DTileGroup(this, tiles.toArray(Tile[]::new));
        this.groups.add(group);

        tiles.forEach(tile -> tilemap.put(tile.position(), group));

        return group;
    }

    @Override
    public TileGroup[] unmerge(TileGroup group) {
        this.groups.remove((DTileGroup) group);

        List<TileGroup> result = new ArrayList<>();
        for ( Tile tile : group.tiles() ) {
            DTileGroup tg = new DTileGroup(this, tile);
            groups.add(tg);
            result.add(tg);

            tilemap.put(tile.position(), tg);
        }

        return result.toArray(TileGroup[]::new);
    }

    //

    private final static Random random = new Random();

    @Override
    public void merge(int size) {
        new HashSet<>(groups).forEach(this::unmerge);

        Set<TileGroup> groups = new HashSet<>(groups());
        Set<TileGroup> frontier = new HashSet<>();

        int radius = 0;
        while ( !groups.isEmpty() && radius < 100 ) {
            frontier.removeIf(tg -> tg.tiles().size() >= size);

            // get contouring tiles of current radius
            Set<TileGroup> contour = new HashSet<>();
            for ( int rx = -1; rx <= 1; rx += 2) {
                int x = radius * rx;
                for ( int z = -radius; z <= radius; z++ ) {
                    groupAt(x, z).ifPresent(contour::add);
                }
            }
            for ( int rz = -1; rz <= 1; rz += 2) {
                int z = radius * rz;
                for ( int x = -radius; x <= radius; x++ ) {
                    groupAt(x, z).ifPresent(contour::add);
                }
            }

            // track groups that haven't been processed yet
            contour.removeIf(g -> !groups.contains(g));
            contour.forEach(groups::remove);

            List<TileGroup> shufcontour = new ArrayList<>(contour);
            Collections.shuffle(shufcontour);

            // add new groups to the frontier
            for ( TileGroup group : shufcontour ) {
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
                TileGroup replacement = merge(target, group);

                frontier.remove(target);
                frontier.remove(group);

                if ( replacement.tiles().size() < size ) {
                    frontier.add(replacement);
                }
            }

            radius++;

            System.out.println("contour: " + contour.size());
            System.out.println("total: " + groups.size());
            System.out.println();
        }
    }

}
