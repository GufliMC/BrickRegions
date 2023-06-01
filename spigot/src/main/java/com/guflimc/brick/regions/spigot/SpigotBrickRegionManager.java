package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.common.AbstractRegionManager;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.common.EventManager;
import com.guflimc.brick.regions.spigot.api.SpigotRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SpigotBrickRegionManager extends AbstractRegionManager<Player> implements SpigotRegionManager {

    protected SpigotBrickRegionManager(BrickRegionsDatabaseContext databaseContext) {
        super(databaseContext);

        for (World world : Bukkit.getWorlds()) {
            loadWorld(world.getUID());
        }

        EventManager.INSTANCE = new SpigotEventManager();
    }

    //

//    @Override
//    public Collection<Locality> localitiesAt(@NotNull Location position) {
//        return localitiesAt(SpigotMath.toBrickLocation(position));
//    }

    @Override
    public Collection<Region> regionsAt(@NotNull Location position) {
        return regionsAt(SpigotMath.toBrickLocation(position));
    }

//    @Override
//    public Optional<TileRegion> tileRegionAt(@NotNull Location location) {
//        return tileRegionAt(SpigotMath.toBrickLocation(location));
//    }
//
//    @Override
//    public Optional<TileGroup> tileGroupAt(@NotNull Location location) {
//        return tileGroupAt(SpigotMath.toBrickLocation(location));
//    }

    @Override
    public Collection<Region> regions(@NotNull World world) {
        return regions(world.getUID());
    }

    @Override
    public Region.World world(@NotNull World world) {
        return world(world.getUID());
    }

}
