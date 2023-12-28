package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.math.common.geometry.pos2.Point2;
import com.guflimc.brick.math.common.geometry.pos2.Vector2;
import com.guflimc.brick.math.common.geometry.pos3.Point3;
import com.guflimc.brick.math.common.geometry.shape2d.Shape2;
import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.brick.regions.api.domain.tile.TileKey;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import com.guflimc.brick.regions.common.EventManager;
import io.ebean.annotation.DbDefault;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Inheritance
public abstract class DTileRegion extends DNamedPropertyRegion implements TileRegion {

    @OneToMany(targetEntity = DTileGroup.class, mappedBy = "region",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    protected List<DTileGroup> groups = new ArrayList<>();

    @Column(name = "tileregion_tile_radius")
    @DbDefault("0")
    protected int radius;

    @Column(name = "tileregion_tile_offset")
    @Convert(converter = Point2Converter.class)
    protected Point2 offset = new Vector2(0, 0);

    protected transient final Map<TileKey, DTileGroup> tilemap = new HashMap<>();

    public DTileRegion() {
        super();
    }

    public DTileRegion(@NotNull UUID worldId, @NotNull String name, int radius) {
        super(worldId, name);
        this.radius = radius;

        setAttribute(RegionAttributeKey.MAP_STROKE_WEIGHT, 1);
    }

    //

    @PostLoad
    public void maptiles() {
        for (DTileGroup group : groups) {
            group.tiles().forEach(tile -> tilemap.put(tile, group));
        }
    }

    //

    @Override
    public boolean contains(@NotNull Point3 point) {
        return groupAt(point).isPresent();
    }

    @Override
    public Optional<TileGroup> groupAt(@NotNull TileKey key) {
        return Optional.ofNullable(tilemap.get(key));
    }

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
        group.tiles().forEach(tilemap::remove);
        EventManager.INSTANCE.onPropertyChange(this);
    }

    @Override
    public TileGroup merge(@NotNull TileGroup... groups) {
        // TODO check adjacency

        Set<TileKey> tiles = Arrays.stream(groups)
                .flatMap(g -> g.tiles().stream())
                .collect(Collectors.toSet());

        Arrays.stream(groups).map(g -> (DTileGroup) g).forEach(this::removeGroup);
        DTileGroup group = new DTileGroup(this, tiles.toArray(TileKey[]::new));
        this.groups.add(group);

        tiles.forEach(tile -> tilemap.put(tile, group));

        EventManager.INSTANCE.onPropertyChange(this);
        return group;
    }

    @Override
    public TileGroup[] unmerge(@NotNull TileGroup group) {
        removeGroup(group);

        List<TileGroup> result = new ArrayList<>();
        for (TileKey tile : group.tiles()) {
            DTileGroup tg = new DTileGroup(this, tile);
            groups.add(tg);
            result.add(tg);

            tilemap.put(tile, tg);
        }

        EventManager.INSTANCE.onPropertyChange(this);
        return result.toArray(TileGroup[]::new);
    }

    //

    @Override
    public void groupify(int size) {
        new HashSet<>(groups).forEach(this::unmerge);

        Set<TileGroup> groups = new HashSet<>(groups());
        Set<TileGroup> frontier = new HashSet<>();

        int radius = 0;
        while (!groups.isEmpty() && radius < 100) {
            frontier.removeIf(tg -> tg.tiles().size() >= size);

            // get contouring tiles of current radius
            Set<TileGroup> contour = new HashSet<>();
            for (int rx = -1; rx <= 1; rx += 2) {
                int x = radius * rx;
                for (int z = -radius; z <= radius; z++) {
                    groupAt(new TileKey(x, z)).ifPresent(contour::add);
                }
            }
            for (int rz = -1; rz <= 1; rz += 2) {
                int z = radius * rz;
                for (int x = -radius; x <= radius; x++) {
                    groupAt(new TileKey(x, z)).ifPresent(contour::add);
                }
            }

            // track groups that haven't been processed yet
            contour.removeIf(g -> !groups.contains(g));
            contour.forEach(groups::remove);

            List<TileGroup> shufcontour = new ArrayList<>(contour);
            Collections.shuffle(shufcontour);

            // add new groups to the frontier
            for (TileGroup group : shufcontour) {
                List<TileGroup> adjacent = adjacent(group).stream()
                        .filter(frontier::contains)
                        .distinct()
                        .toList();

                if (adjacent.isEmpty()) {
                    frontier.add(group);
                    continue;
                }

                TileGroup target = adjacent.stream().min(Comparator.comparingInt(tg -> tg.tiles().size())).orElseThrow();
                TileGroup replacement = merge(target, group);

                frontier.remove(target);
                frontier.remove(group);

                if (replacement.tiles().size() < size) {
                    frontier.add(replacement);
                }
            }

            radius++;
        }
    }

}
