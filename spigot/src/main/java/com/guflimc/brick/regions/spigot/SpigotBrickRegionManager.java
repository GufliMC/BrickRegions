package com.guflimc.brick.regions.spigot;

import com.guflimc.brick.regions.common.AbstractRegionManager;
import com.guflimc.brick.regions.common.BrickRegionsDatabaseContext;
import com.guflimc.brick.regions.spigot.api.SpigotRegionManager;
import org.bukkit.entity.Player;

public class SpigotBrickRegionManager extends AbstractRegionManager<Player> implements SpigotRegionManager {

    protected SpigotBrickRegionManager(BrickRegionsDatabaseContext databaseContext) {
        super(databaseContext);
    }

}
