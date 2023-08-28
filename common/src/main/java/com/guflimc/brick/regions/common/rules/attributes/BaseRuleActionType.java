package com.guflimc.brick.regions.common.rules.attributes;

import com.guflimc.brick.regions.api.rules.attributes.RuleActionType;
import org.jetbrains.annotations.NotNull;

public record BaseRuleActionType(@NotNull String name) implements RuleActionType {
}
