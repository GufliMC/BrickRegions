package com.guflimc.brick.regions.api.rules;

import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public abstract class RuleTarget<S> {

    private final static Map<String, RuleTarget<?>> targets = new ConcurrentHashMap<>();

    //

    private final String name;
    private final int priority;

    private final Class<S> type;
    private final BiPredicate<S, Region> predicate;

    public RuleTarget(String name, int priority, Class<S> type, BiPredicate<S, Region> predicate) {
        this.name = name;
        this.priority = priority;
        this.type = type;
        this.predicate = predicate;
        targets.put(name, this);
    }

    public RuleTarget(String name, Class<S> type, BiPredicate<S, Region> predicate) {
        this(name, 0, type, predicate);
    }

    public final String name() {
        return name;
    }

    public final int priority() {
        return priority;
    }

    public final boolean test(S subject, Region region) {
        return predicate.test(subject, region);
    }

    public final boolean testAny(Object subject, Region region) {
        return type.isAssignableFrom(subject.getClass()) && test((S) subject, region);
    }

    //

    public static @Nullable RuleTarget<?> valueOf(String name) {
        return targets.get(name);
    }

    public static RuleTarget<?>[] values() {
        return targets.values().toArray(RuleTarget[]::new);
    }

}
