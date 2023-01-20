package com.guflimc.brick.regions.spigot.api;

import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.TileRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface SpigotRegionManager extends RegionManager<Player> {

    Collection<Locality> localitiesAt(@NotNull Location position);

    default Collection<Locality> localitiesAt(@NotNull Block block) {
        return localitiesAt(block.getLocation());
    }

    default Collection<Locality> localitiesAt(@NotNull Entity entity) {
        return localitiesAt(entity.getLocation());
    }

    //

    Collection<Region> regionsAt(@NotNull Location position);

    default Collection<Region> regionsAt(@NotNull Block block) {
        return regionsAt(block.getLocation());
    }

    default Collection<Region> regionsAt(@NotNull Entity entity) {
        return regionsAt(entity.getLocation());
    }

    //

    Collection<TileRegion> regionsTiledAt(@NotNull Location location);

    default Collection<TileRegion> regionsTiledAt(@NotNull Entity entity) {
        return regionsTiledAt(entity.getLocation());
    }

    default Collection<TileRegion> regionsTiledAt(@NotNull Block block) {
        return regionsTiledAt(block.getLocation());
    }

    //

    Collection<Region> regions(@NotNull World world);

    Region globalRegion(@NotNull World world);

}
