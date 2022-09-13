package com.guflimc.brick.regions.common.domain;

import com.guflimc.brick.regions.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("REGION")
@DiscriminatorColumn(name = "type")
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

    @Column(nullable = false)
    @ColumnDefault("1")
    private int priority = 1;

    @OneToMany(targetEntity = DRegionAttribute.class, mappedBy = "region",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DRegionAttribute> attributes = new ArrayList<>();

//    private transient final Set<Rule<?>> rules = new CopyOnWriteArraySet<>();

    public DRegion() {
    }

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
    public int priority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    private <T> void removeAttribute(AttributeKey<T> key) {
        attributes.removeIf(attr -> attr.name().equals(key.name()));
    }

    @Override
    public <T> void setAttribute(AttributeKey<T> key, T value) {
        if (value == null) {
            removeAttribute(key);
            return;
        }

        attributes.stream().filter(attr -> attr.name().equals(key.name())).findFirst()
                .ifPresentOrElse(
                        attr -> attr.setValue(key.serialize(value)),
                        () -> attributes.add(new DRegionAttribute(this, key.name(), key.serialize(value)))
                );
    }

    @Override
    public <T> Optional<T> attribute(AttributeKey<T> key) {
        return attributes.stream().filter(attr -> attr.name().equals(key.name())).findFirst()
                .map(ra -> key.deserialize(ra.value()));
    }

//    @Override
//    public <P> Rule<P> addRule(int priority, RuleStatus status, Predicate<P> predicate, RuleType... ruleTypes) {
//        Rule<P> rule = new Rule<P>(priority, status, (p, region) -> predicate.test(p), ruleTypes);
//        rules.add(rule);
//        return rule;
//    }
//
//    @Override
//    public <P> void removeRule(Rule<P> rule) {
//        rules.remove(rule);
//    }
//
//    @Override
//    public Collection<Rule<?>> rules() {
//        return Collections.unmodifiableSet(rules);
//    }
}
