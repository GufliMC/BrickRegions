package com.guflimc.brick.regions.spigot.api;

import com.guflimc.brick.regions.api.RegionAPI;

public class SpigotRegionAPI {

    private static SpigotRegionManager regionManager;

    public static void setRegionManager(SpigotRegionManager manager) {
        regionManager = manager;
        RegionAPI.setRegionManager(manager);
    }

    //

    public static SpigotRegionManager get() {
        return regionManager;
    }

}
