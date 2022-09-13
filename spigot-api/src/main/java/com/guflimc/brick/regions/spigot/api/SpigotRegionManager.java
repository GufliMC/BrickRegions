package com.guflimc.brick.regions.spigot.api;

import com.guflimc.brick.regions.api.RegionManager;
import com.guflimc.brick.regions.api.domain.Region;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface SpigotRegionManager extends RegionManager<Player> {

    Collection<Region> regionsAt(@NotNull Location position);

    Collection<Region> regionsAt(@NotNull Entity entity);

}
