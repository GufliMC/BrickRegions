package com.guflimc.brick.regions.common;

import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;
import com.guflimc.brick.regions.common.domain.*;

public class BrickRegionsDatabaseContext extends HibernateDatabaseContext {

    public BrickRegionsDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public BrickRegionsDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[]{
                DRegion.class,
                DAreaRegion.class,
                DWorldRegion.class,
                DRegionAttribute.class,
                DRegionProtectionRule.class
        };
    }

}