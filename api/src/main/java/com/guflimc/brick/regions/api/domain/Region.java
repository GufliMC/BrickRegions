package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.attributes.AttributeKey;

import java.util.Optional;
import java.util.UUID;

public interface Region {

    UUID id();

    UUID worldId();

    String name();

    int priority();

    void setPriority(int priority);

    <T> void setAttribute(AttributeKey<T> key, T value);

    <T> Optional<T> attribute(AttributeKey<T> key);

//
//    <P> Rule<P> addRule(int priority, RuleStatus status, Predicate<P> predicate, RuleType... ruleTypes);
//
//    <P> void removeRule(Rule<P> rule);
//
//    Collection<Rule<?>> rules();

}
