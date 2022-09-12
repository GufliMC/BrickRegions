package com.guflimc.brick.regions.api.rules;

import com.guflimc.brick.regions.api.domain.Region;

import java.util.function.BiPredicate;

public record Rule(int priority, RuleStatus status, BiPredicate<Object, Region> predicate, RuleType... ruleTypes) {}
