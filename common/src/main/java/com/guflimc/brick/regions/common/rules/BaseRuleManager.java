package com.guflimc.brick.regions.common.rules;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.RuleManager;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleActionType;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
import com.guflimc.brick.regions.common.rules.attributes.BaseRuleAction;
import com.guflimc.brick.regions.common.rules.attributes.BaseRuleActionType;
import com.guflimc.brick.regions.common.rules.attributes.BaseRuleCondition;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class BaseRuleManager<S> implements RuleManager<S> {

    private final Map<String, BaseRuleCondition<S>> conditions = new ConcurrentHashMap<>();
    private final Map<String, RuleActionType> actionTypes = new ConcurrentHashMap<>();
    private final Map<String, RuleAction> actions = new ConcurrentHashMap<>();

    //

    @Override
    public RuleCondition registerCondition(@NotNull String name, @NotNull BiPredicate<S, Region> condition, int priority) {
        if (condition(name).isPresent()) {
            throw new IllegalArgumentException("Condition with name " + name + " already exists");
        }
        BaseRuleCondition<S> rc = new BaseRuleCondition<>(name, condition, priority);
        conditions.put(name, rc);
        return rc;
    }

    @Override
    public Optional<RuleCondition> condition(@NotNull String name) {
        return Optional.ofNullable(conditions.get(name));
    }

    private Optional<BaseRuleCondition<S>> _condition(@NotNull String name) {
        return Optional.ofNullable(conditions.get(name));
    }

    @Override
    public Collection<RuleCondition> conditions() {
        return Set.copyOf(conditions.values());
    }

    //

    @Override
    public RuleActionType registerActionType(@NotNull String name) {
        if (actionType(name).isPresent()) {
            throw new IllegalArgumentException("ActionType with name " + name + " already exists");
        }
        RuleActionType rat = new BaseRuleActionType(name);
        actionTypes.put(name, rat);
        return rat;
    }

    @Override
    public Optional<RuleActionType> actionType(@NotNull String name) {
        return Optional.ofNullable(actionTypes.get(name));
    }

    @Override
    public Collection<RuleActionType> actionTypes() {
        return Set.copyOf(actionTypes.values());
    }

    //

    @Override
    public RuleAction registerAction(@NotNull String name, @NotNull RuleActionType type) {
        if (!actionTypes.containsValue(type)) {
            throw new IllegalArgumentException("ActionType with name " + type.name() + " is not registered.");
        }
        if (action(name, type).isPresent()) {
            throw new IllegalArgumentException("Action with name " + name + " and type " + type.name() + " already exists");
        }
        RuleAction ra = new BaseRuleAction(name, type);
        actions.put(name, ra);
        return ra;
    }

    @Override
    public Optional<RuleAction> action(@NotNull String name, @NotNull RuleActionType type) {
        if (!actionTypes.containsValue(type)) {
            throw new IllegalArgumentException("ActionType with name " + type.name() + " is not registered.");
        }
        return Optional.ofNullable(actions.get(name));
    }

    @Override
    public Collection<RuleAction> actions() {
        return Set.copyOf(actions.values());
    }

    @Override
    public Collection<RuleAction> actions(@NotNull RuleActionType type) {
        if (!actionTypes.containsValue(type)) {
            throw new IllegalArgumentException("ActionType with name " + type.name() + " is not registered.");
        }
        return actions.values().stream()
                .filter(ra -> ra.type().equals(type))
                .collect(Collectors.toUnmodifiableSet());
    }

    //

    @Override
    public Optional<Rule> match(@NotNull S subject, @NotNull Collection<Region> regions, @NotNull RuleAction action) {
        return best(regions.stream()
                .filter(Region.Ruleable.class::isInstance)
                .map(Region.Ruleable.class::cast)
                .flatMap(rg -> rg.rules().stream()) // flat map to all rules
                .filter(rule -> rule.action().matches(action)) // filter by matching action
                .filter(rule -> _condition(rule.condition().name()).map(rc -> rc.condition().test(subject, rule.region())).orElse(false)) // filter by condition
                .toList());
    }

    /**
     * Returns the best matching rule based on multiple priorities:
     * region priority > rule priority > condition priority > type (specific vs wildcard) > target (specific vs wildcard) > status (allow vs deny)
     */
    private Optional<Rule> best(@NotNull Collection<Rule> rules) {
        return rules.stream().max(Comparator
                .comparingInt((Rule r) -> r.region().priority()) // region
                .thenComparingInt(Rule::priority) // rule
                .thenComparingInt(r -> r.condition().priority()) // target
                //.thenComparingInt(r -> r.type() == RuleActionBase.ALL ? 0 : 1) // type (specific > ALL)
                .thenComparingInt(r -> r.action().name().equals("*") ? 0 : 1) // target (specific > wildcard)
                .thenComparingInt(r -> r.status().ordinal()) // status (ALLOW > DENY)
        );
    }

    @Override
    public RuleStatus status(@NotNull S subject, @NotNull RuleAction action, @NotNull Collection<Region> regions) {
        return match(subject, regions, action).map(Rule::status).orElse(RuleStatus.ALLOW);
    }

}
