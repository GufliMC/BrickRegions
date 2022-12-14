package com.guflimc.brick.regions.spigot.api;

import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface SpigotRegionManager extends RegionManager<Player> {

    Collection<Region> regionsAt(@NotNull Location position);

    default Collection<Region> regionsAt(@NotNull Block block) {
        return regionsAt(block.getLocation());
    }

    default Collection<Region> regionsAt(@NotNull Entity entity) {
        return regionsAt(entity.getLocation());
    }

    Collection<Region> regions(@NotNull World world);

    Region globalRegion(@NotNull World world);

}
