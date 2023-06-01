package com.guflimc.brick.regions.api.rules;

import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public abstract class RuleTarget {

    private static final Map<String, RuleTarget> REGISTRY = new ConcurrentHashMap<>();

    //

    public static final RuleTarget ANY = register("ANY", Object.class, (s, r) -> true);

    //

    private final String name;
    private final int priority;

    private RuleTarget(String name, int priority) {
        this.name = name;
        this.priority = priority;
        REGISTRY.put(name(), this);
    }

    public final String name() {
        return name;
    }

    public final int priority() {
        return priority;
    }

    public abstract boolean test(Object subject, Region region);

    //

    public static @Nullable RuleTarget valueOf(String name) {
        return REGISTRY.get(name);
    }

    public static RuleTarget[] values() {
        return REGISTRY.values().toArray(RuleTarget[]::new);
    }

    //

    public static <T> RuleTarget register(String name, int priority, Class<T> type, BiPredicate<T, Region> predicate) {
        return new SimpleRuleTarget<>(name, priority, type, predicate);
    }

    public static <T> RuleTarget register(String name, Class<T> type, BiPredicate<T, Region> predicate) {
        return register(name, 0, type, predicate);
    }

    private static class SimpleRuleTarget<S> extends RuleTarget {

        //

        private final Class<S> type;
        private final BiPredicate<S, Region> predicate;

        SimpleRuleTarget(String name, int priority, Class<S> type, BiPredicate<S, Region> predicate) {
            super(name, priority);
            this.type = type;
            this.predicate = predicate;
        }

        @Override
        public boolean test(Object subject, Region region) {
            return type.isInstance(subject) && predicate.test(type.cast(subject), region);
        }

    }
}
