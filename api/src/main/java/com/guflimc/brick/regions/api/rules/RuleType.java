package com.guflimc.brick.regions.api.rules;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record RuleType(String name) {

    private final static Map<String, RuleType> types = new ConcurrentHashMap<>();

    //

    public static final RuleType ALL = new RuleType("ALL");

    public static final RuleType BUILD = new RuleType("BUILD");
    public static final RuleType CONTAINER = new RuleType("CONTAINER");
    public static final RuleType INTERACT = new RuleType("INTERACT");
    public static final RuleType ATTACK_NEUTRAL_MOBS = new RuleType("ATTACK_NEUTRAL_MOBS");
    public static final RuleType ATTACK_HOSTILE_MOBS = new RuleType("ATTACK_HOSTILE_MOBS");

    public static final RuleType COLLECT_ITEMS = new RuleType("COLLECT_ITEMS");

    // TODO handlers for everything below
//    public static final RuleType DROP_ITEMS = new RuleType("DROP_ITEMS");
//    public static final RuleType PVP = new RuleType("PVP");
//    public static final RuleType FLY = new RuleType("FLY");
//    public static final RuleType TELEPORT = new RuleType("TELEPORT");
//    public static final RuleType COMMAND = new RuleType("COMMAND");

    //

    public RuleType(String name) {
        this.name = name.toUpperCase();
        types.put(this.name, this);
    }

    public static @Nullable RuleType valueOf(String name) {
        return types.get(name.toUpperCase());
    }

    public static RuleType[] values() {
        return types.values().toArray(RuleType[]::new);
    }
}
