package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.maths.api.geo.area.Area;
import com.guflimc.brick.maths.spigot.api.SpigotMaths;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.common.AbstractRegionManager;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.common.domain.DRegion;
import com.guflimc.brick.regions.spigot.api.SpigotRegionManager;
import com.guflimc.brick.regions.spigot.api.events.RegionCreateEvent;
import com.guflimc.brick.regions.spigot.api.events.RegionDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotBrickRegionManager extends AbstractRegionManager<Player> implements SpigotRegionManager {

    protected SpigotBrickRegionManager(BrickRegionsDatabaseContext databaseContext) {
        super(databaseContext);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Region region) {
        Bukkit.getServer().getPluginManager().callEvent(new RegionDeleteEvent(region, !Bukkit.isPrimaryThread()));
        return super.remove(region);
    }

    @Override
    public CompletableFuture<Region> create(@NotNull String name, @NotNull UUID worldId, @NotNull Area area) {
        return super.create(name, worldId, area).thenApply(rg -> {
            Bukkit.getServer().getPluginManager().callEvent(new RegionCreateEvent(rg));
            return rg;
        });
    }

    //

    @Override
    public void addRule(PersistentRegion region, RuleStatus status, RuleTarget<Player> target, RuleType... type) {
        ((DRegion) region).addRule(status, target, type);
    }

    @Override
    public void addRule(PersistentRegion region, int priority, RuleStatus status, RuleTarget<Player> target, RuleType... type) {
        ((DRegion) region).addRule(priority, status, target, type);
    }

    //

    @Override
    public Collection<Region> regionsAt(@NotNull Location position) {
        return regionsAt(SpigotMaths.toBrickLocation(position));
    }

    @Override
    public Collection<Region> regionsAt(@NotNull Entity entity) {
        return regionsAt(entity.getLocation());
    }

}
