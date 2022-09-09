package com.guflimc.brick.regions.api;

public class RegionAPI {

    private static RegionManager<?> regionManager;

    public static void setRegionManager(RegionManager<?> manager) {
        regionManager = manager;
    }

    //

    public static RegionManager<?> get() {
        return regionManager;
    }

}
