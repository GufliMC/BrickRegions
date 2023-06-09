package com.guflimc.brick.regions.common;

import com.guflimc.brick.orm.ebean.database.EbeanConfig;

public class BrickRegionsConfig {

    public EbeanConfig database = new EbeanConfig();

    public BrickRegionsConfig() {
        database.dsn = "jdbc:h2:file:./plugins/BrickRegions/data/database.h2;MODE=MySQL";
        database.driver = "org.h2.Driver";
        database.username = "user";
        database.password = "";
    }

}
