package com.guflimc.brick.regions.api.rules;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleActionType;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface RuleManager<S> {

    // CONDITIONS

    RuleCondition registerCondition(@NotNull String name, @NotNull BiPredicate<S, Region> condition, int priority);

    default RuleCondition registerCondition(@NotNull String name, @NotNull BiPredicate<S, Region> condition) {
        return registerCondition(name, condition, 0);
    }

    Optional<RuleCondition> condition(@NotNull String name);

    Collection<RuleCondition> conditions();

    // ACTION TYPES

    RuleActionType registerActionType(@NotNull String name);

    Optional<RuleActionType> actionType(@NotNull String name);

    Collection<RuleActionType> actionTypes();

    // ACTIONS

    RuleAction registerAction(@NotNull String name, @NotNull RuleActionType type);

    Optional<RuleAction> action(@NotNull String name, @NotNull RuleActionType type);

    Collection<RuleAction> actions();

    Collection<RuleAction> actions(@NotNull RuleActionType type);

    // MATCHING

    /**
     * Returns the best matching rule from the given regions that applies to the given subject and is of the given type.
     */
    Optional<Rule> match(@NotNull S subject, @NotNull Collection<Region> regions, @NotNull RuleAction action);

    /**
     * Returns if the given action is ALLOWED or DENIED for the given subject in the given regions.
     */
    RuleStatus status(@NotNull S subject, @NotNull RuleAction action, @NotNull Collection<Region> regions);
}
