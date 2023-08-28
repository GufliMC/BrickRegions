package com.guflimc.brick.regions.common.rules.attributes;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public record BaseRuleCondition<S>(@NotNull String name, @NotNull BiPredicate<S, Region> condition, int priority) implements RuleCondition {
}
