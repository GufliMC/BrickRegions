package com.guflimc.brick.regions.common.rules.attributes;

import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleActionType;
import org.jetbrains.annotations.NotNull;

public record BaseRuleAction(@NotNull String name, @NotNull RuleActionType type) implements RuleAction {
}
