package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.math.common.geometry.shape3d.Shape3;
import com.guflimc.brick.math.spigot.SpigotMath;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.common.AbstractRegionManager;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.common.domain.DWorldRegion;
import com.guflimc.brick.regions.spigot.api.SpigotRegionManager;
import com.guflimc.brick.regions.spigot.api.events.RegionCreateEvent;
import com.guflimc.brick.regions.spigot.api.events.RegionDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotBrickRegionManager extends AbstractRegionManager<Player> implements SpigotRegionManager {

    protected SpigotBrickRegionManager(BrickRegionsDatabaseContext databaseContext) {
        super(databaseContext);

        for (World world : Bukkit.getWorlds()) {
            loadWorld(world.getUID());
        }
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Locality locality) {
        if (locality instanceof DWorldRegion) {
            throw new IllegalArgumentException("Cannot delete the global region.");
        }
        if (locality instanceof Region region) {
            Bukkit.getServer().getPluginManager().callEvent(new RegionDeleteEvent(region, !Bukkit.isPrimaryThread()));
        }
        return super.remove(locality);
    }

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Shape3 shape) {
        return super.create(name, worldId, shape).thenApply(rg -> {
            Bukkit.getServer().getPluginManager().callEvent(new RegionCreateEvent(rg));
            return rg;
        });
    }

    //

    @Override
    public Collection<Locality> localitiesAt(@NotNull Location position) {
        return localitiesAt(SpigotMath.toBrickLocation(position));
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Location position) {
        return regionsAt(SpigotMath.toBrickLocation(position));
    }

    @Override
    public Collection<Region> regions(@NotNull World world) {
        return regions(world.getUID());
    }

    @Override
    public Region globalRegion(@NotNull World world) {
        return worldRegion(world.getUID());
    }

}
