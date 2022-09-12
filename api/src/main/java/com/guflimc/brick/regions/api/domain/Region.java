package com.guflimc.brick.regions.api.domain;

import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleType;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

public interface Region {

    UUID id();

    UUID worldId();

    String name();

    <P> Rule<P> addRule(int priority, RuleStatus status, Predicate<P> predicate, RuleType... ruleTypes);

    <P> void removeRule(Rule<P> rule);

    Collection<Rule<?>> rules();

}
