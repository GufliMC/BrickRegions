package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorValue("REGION")
@DiscriminatorColumn(name="type")
@Table(name = "regions")
public class DRegion implements Region {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID worldId;

    @Column(nullable = false, unique = true)
    private String name;

    private transient final Set<Rule<?>> rules = new CopyOnWriteArraySet<>();

    public DRegion() {}

    public DRegion(UUID worldId, String name) {
        this.worldId = worldId;
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    public UUID worldId() {
        return worldId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public <P> Rule<P> addRule(int priority, RuleStatus status, Predicate<P> predicate, RuleType... ruleTypes) {
        Rule<P> rule = new Rule<P>(priority, status, (p, region) -> predicate.test(p), ruleTypes);
        rules.add(rule);
        return rule;
    }

    @Override
    public <P> void removeRule(Rule<P> rule) {
        rules.remove(rule);
    }

    @Override
    public Collection<Rule<?>> rules() {
        return Collections.unmodifiableSet(rules);
    }
}
