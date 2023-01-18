package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
@Table(name = "locality_rules")
@Index(columnNames = {"locality_id", "status", "target", "type_set"}, unique = true)
public class DLocalityProtectionRule implements LocalityProtectionRule {

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
    private RuleTarget<?> target;

    @Column(nullable = false)
    @Convert(converter = RuleTypeSetConverter.class)
    private RuleTypeSet typeSet;

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DLocality locality;

    public DLocalityProtectionRule() {
    }

    public DLocalityProtectionRule(DLocality locality, int priority, RuleStatus status, RuleTarget<?> target, RuleType... types) {
        this.locality = locality;
        this.priority = priority;
        this.status = status;
        this.target = target;
        this.typeSet = new RuleTypeSet(types);
    }

    public DLocalityProtectionRule(DLocality locality, RuleStatus status, RuleTarget<?> target, RuleType... types) {
        this(locality, 0, status, target, types);
    }

    public DLocality locality() {
        return locality;
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

    public static class RuleTargetConverter implements AttributeConverter<RuleTarget<?>, String> {
        @Override
        public String convertToDatabaseColumn(RuleTarget<?> attribute) {
            return attribute.name();
        }

        @Override
        public RuleTarget<?> convertToEntityAttribute(String dbData) {
            return RuleTarget.valueOf(dbData);
        }
    }

}
