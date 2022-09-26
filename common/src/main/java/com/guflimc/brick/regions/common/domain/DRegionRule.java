package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.RegionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "region_rules"
)
public class DRegionRule implements RegionRule {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DRegion region;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleStatus status;

    @Column(nullable = false)
    @Convert(converter = RuleTargetConverter.class)
    private RuleTarget<?> target;

    @Column(nullable = false)
    @Convert(converter = RuleTypesConverter.class)
    private RuleType[] types;

    public DRegionRule() {
    }

    public DRegionRule(DRegion region, int priority, RuleStatus status, RuleTarget<?> target, RuleType... types) {
        this.region = region;
        this.priority = priority;
        this.status = status;
        this.target = target;
        this.types = types;
    }

    public DRegionRule(DRegion region, RuleStatus status, RuleTarget<?> target, RuleType... types) {
        this(region, 0, status, target, types);
    }

    public Region region() {
        return region;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public RuleStatus status() {
        return status;
    }

    @Override
    public RuleTarget<?> target() {
        return target;
    }

    @Override
    public RuleType[] types() {
        return types;
    }

    //

    public static class RuleTypesConverter implements AttributeConverter<RuleType[], String> {

        private final static String PATTERN = Pattern.quote(", ");

        @Override
        public String convertToDatabaseColumn(RuleType[] attribute) {
            return Arrays.stream(attribute).map(RuleType::name).collect(Collectors.joining(","));
        }

        @Override
        public RuleType[] convertToEntityAttribute(String dbData) {
            return Arrays.stream(dbData.split(PATTERN)).map(RuleType::valueOf).filter(Objects::nonNull).toArray(RuleType[]::new);
        }
    }

    public static class RuleTargetConverter implements AttributeConverter<RuleTarget<?>, String> {
        @Override
        public String convertToDatabaseColumn(RuleTarget<?> attribute) {
            return attribute.name();
        }

        @Override
        public RuleTarget<?> convertToEntityAttribute(String dbData) {
            return RuleTarget.byName(dbData).orElse(null);
        }
    }

}
