package com.guflimc.brick.regions.common;

import com.guflimc.brick.math.database.Point2Converter;
import com.guflimc.brick.math.database.Shape2Converter;
import com.guflimc.brick.math.database.Shape3Converter;
import com.guflimc.brick.orm.ebean.database.EbeanConfig;
import com.guflimc.brick.orm.ebean.database.EbeanDatabaseContext;
import com.guflimc.brick.orm.ebean.database.EbeanMigrations;
import com.guflimc.brick.orm.jpa.converters.ComponentConverter;
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
            DLocality.class,
            DLocalityAttribute.class,
            DLocalityProtectionRule.class,
            DRegion.class,
            DShapeRegion.class,
            DTile.class,
            DWorldRegion.class,
            DTileRegion.class,

            Shape3Converter.class,
            Shape2Converter.class,
            Point2Converter.class,
            DLocalityProtectionRule.RuleTargetConverter.class,
            DLocalityProtectionRule.RuleTypeSetConverter.class,
            ComponentConverter.class
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
