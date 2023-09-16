package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.attribute.RegionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
@Table(name = "region_rules")
@Index(columnNames = {"region_id", "status", "target", "types"}, unique = true)
public class DRegionRule implements RegionRule {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @DbDefault("0")
    private int priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleStatus status;

    @Column(nullable = false)
    @Convert(converter = RuleTargetConverter.class)
    private RuleTarget target;

    @Column(nullable = false, name = "types")
    @Convert(converter = RuleTypeSetConverter.class)
    private RuleTypeSet typeSet;

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    @JoinColumn(name = "region_id")
    private DRegion region;

    public DRegionRule() {
    }

    public DRegionRule(DRegion region, int priority, RuleStatus status, RuleTarget target, RuleType... types) {
        this.region = region;
        this.priority = priority;
        this.status = status;
        this.target = target;
        this.typeSet = new RuleTypeSet(types);
    }

    public DRegionRule(DRegion region, RuleStatus status, RuleTarget target, RuleType... types) {
        this(region, 0, status, target, types);
    }

    public DRegion region() {
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
    public RuleTarget target() {
        return target;
    }

    @Override
    public RuleType[] types() {
        return typeSet.types;
    }

    @Override
    public String toString() {
        return status.name() + " " + target.name() + " " + Arrays.stream(typeSet.types).map(RuleType::name)
                .collect(Collectors.joining(", "));
    }

    //

    private record RuleTypeSet(RuleType[] types) {
    }

    public static class RuleTypeSetConverter implements AttributeConverter<RuleTypeSet, String> {

        private final static String PATTERN = Pattern.quote(", ");

        @Override
        public String convertToDatabaseColumn(RuleTypeSet attribute) {
            return Arrays.stream(attribute.types).map(RuleType::name).collect(Collectors.joining(","));
        }

        @Override
        public RuleTypeSet convertToEntityAttribute(String dbData) {
            return new RuleTypeSet(Arrays.stream(dbData.split(PATTERN))
                    .map(RuleType::valueOf)
                    .filter(Objects::nonNull)
                    .toArray(RuleType[]::new));
        }
    }

    public static class RuleTargetConverter implements AttributeConverter<RuleTarget, String> {
        @Override
        public String convertToDatabaseColumn(RuleTarget attribute) {
            return attribute.name();
        }

        @Override
        public RuleTarget convertToEntityAttribute(String dbData) {
            return RuleTarget.valueOf(dbData);
        }
    }

}
