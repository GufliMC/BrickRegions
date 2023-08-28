package com.guflimc.brick.regions.api.rules.attributes;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface RuleAction {

    String name();

    RuleActionType type();

    default boolean matches(@NotNull RuleAction other) {
        return Objects.equals(type(), other.type()) && (name().equals("*") || Objects.equals(name(), other.name()));
    }

}
