package com.guflimc.brick.regions.common;

import com.guflimc.brick.maths.database.api.AreaConverter;
import com.guflimc.brick.orm.ebean.database.EbeanConfig;
import com.guflimc.brick.orm.ebean.database.EbeanDatabaseContext;
import com.guflimc.brick.orm.ebean.database.EbeanMigrations;
import com.guflimc.brick.regions.common.domain.*;
import io.ebean.annotation.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;

public class BrickRegionsDatabaseContext extends EbeanDatabaseContext {

    public BrickRegionsDatabaseContext(EbeanConfig config) {
        super(config, "BrickRegions");
    }

    public BrickRegionsDatabaseContext(EbeanConfig config, int poolSize) {
        super(config, "BrickRegions", poolSize);
    }

    @Override
    protected Class<?>[] applicableClasses() {
        return APPLICABLE_CLASSES;
    }

    private static final Class<?>[] APPLICABLE_CLASSES = new Class[]{
            DRegion.class,
            DAreaRegion.class,
            DWorldRegion.class,
            DRegionAttribute.class,
            DRegionProtectionRule.class,

            AreaConverter.class,
            DRegionProtectionRule.RuleTargetConverter.class,
            DRegionProtectionRule.RuleTypeSetConverter.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        EbeanMigrations generator = new EbeanMigrations(
                "BrickRegions",
                Path.of("BrickRegions/common/src/main/resources"),
                Platform.H2, Platform.MYSQL
        );
        Arrays.stream(APPLICABLE_CLASSES).forEach(generator::addClass);
        generator.generate();
    }
}
