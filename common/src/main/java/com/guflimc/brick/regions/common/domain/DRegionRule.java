package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleActionType;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
import io.ebean.annotation.ConstraintMode;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;
import jakarta.persistence.*;

import java.util.UUID;
import java.util.regex.Pattern;

@Entity
@Table(name = "region_rules")
@Index(columnNames = {"region_id", "status", "condition", "action"}, unique = true)
public class DRegionRule implements Rule {

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
    @Convert(converter = RuleConditionConverter.class)
    private RuleCondition condition;

    @Convert(converter = RuleActionConverter.class)
    private RuleAction action;

    @ManyToOne(optional = false)
    @DbForeignKey(onDelete = ConstraintMode.CASCADE)
    private DRegion region;

    public DRegionRule() {
    }

    public DRegionRule(DRegion region, int priority, RuleStatus status, RuleCondition condition, RuleAction action) {
        this.region = region;
        this.priority = priority;
        this.status = status;
        this.condition = condition;
        this.action = action;
    }

    public DRegionRule(DRegion region, RuleStatus status, RuleCondition condition, RuleAction action) {
        this(region, 0, status, condition, action);
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
    public RuleCondition condition() {
        return condition;
    }

    @Override
    public RuleAction action() {
        return action;
    }

    @Override
    public String toString() {
        return status.name() + " " + condition.name() + " " + action.type().name() + " " + action.name();
    }

    //

    public static class RuleActionConverter implements AttributeConverter<RuleAction, String> {


        @Override
        public String convertToDatabaseColumn(RuleAction attribute) {
            return attribute.type().name() + ";" + attribute.name();
        }

        @Override
        public RuleAction convertToEntityAttribute(String dbData) {
            String[] split = dbData.split(Pattern.quote(";"));
            RuleActionType type = RegionAPI.get().rules().actionType(split[0]).orElseThrow();
            return RegionAPI.get().rules().action(split[1], type).orElseThrow();
        }
    }

    public static class RuleConditionConverter implements AttributeConverter<RuleCondition, String> {

        @Override
        public String convertToDatabaseColumn(RuleCondition attribute) {
            return attribute.name();
        }

        @Override
        public RuleCondition convertToEntityAttribute(String dbData) {
            return RegionAPI.get().rules().condition(dbData).orElseThrow();
        }

    }

}
