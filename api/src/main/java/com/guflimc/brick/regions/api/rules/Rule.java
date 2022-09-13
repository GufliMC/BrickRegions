package com.guflimc.brick.regions.api.rules;

import java.util.Arrays;
import java.util.stream.Collectors;

public record Rule<S>(RuleStatus status, RuleTarget<S> target, RuleType... ruleTypes) {

    @Override
    public String toString() {
        return status.name() + " " + target.name() + " " + Arrays.stream(ruleTypes).map(RuleType::name).collect(Collectors.joining(","));
    }

}
