package com.guflimc.brick.regions.common;

import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;
import com.guflimc.brick.regions.common.domain.DAreaRegion;
import com.guflimc.brick.regions.common.domain.DRegion;
import com.guflimc.brick.regions.common.domain.DRegionAttribute;
import com.guflimc.brick.regions.common.domain.DRegionRule;

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
                DRegionAttribute.class,
                DRegionRule.class
        };
    }

}
