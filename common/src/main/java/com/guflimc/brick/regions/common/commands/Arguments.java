package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.build.Argument;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Arguments {

    // REGIONS

    @Parser(type = Region.class)
    public Argument region(@Source Audience audience, @Source("worldId") UUID worldId, String input) {
        return RegionAPI.get()
                .findRegion(worldId, input)
                .map(Argument::success)
                .orElse(Argument.fail(() -> audience.sendMessage(Component.text("Region not found")))); // TODO
    }

    @Completer
    public List<String> region(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId)
                .stream().map(Region::name).toList();
    }

    //

    @Parser(type = RuleType.class)
    public Argument ruleType(@Source Audience audience, String input) {
        return Optional.ofNullable(RuleType.valueOf(input.toUpperCase()))
                .map(Argument::success)
                .orElse(Argument.fail(() -> audience.sendMessage(Component.text("Rule type not found")))); // TODO
    }

    @Completer
    public List<String> ruleType() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    //

    @Parser(type = RuleTarget.class)
    public Argument ruleTarget(@Source Audience audience, String input) {
        return Optional.ofNullable(RuleType.valueOf(input.toUpperCase()))
                .map(Argument::success)
                .orElse(Argument.fail(() -> audience.sendMessage(Component.text("Rule target not found")))); // TODO
    }

    @Completer
    public List<String> ruleTarget() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    //

    @Parser(type = RuleStatus.class)
    public Argument ruleStatus(@Source Audience audience, String input) {
        return Arrays.stream(RuleStatus.values()).filter(rs -> rs.name().equalsIgnoreCase(input)).findFirst()
                .map(Argument::success)
                .orElse(Argument.fail(() -> audience.sendMessage(Component.text("Rule status not found")))); // TODO
    }

    @Completer
    public List<String> ruleStatus() {
        return Arrays.stream(RuleStatus.values()).map(RuleStatus::name).toList();
    }

    //

    @Parser(type = SelectionType.class)
    public Argument selectionType(@Source Audience audience, String input) {
        return Arrays.stream(SelectionType.values()).filter(st -> st.name().equalsIgnoreCase(input)).findFirst()
                .map(Argument::success)
                .orElse(Argument.fail(() -> audience.sendMessage(Component.text("Selection type not found")))); // TODO
    }

    @Completer
    public List<String> selectionType() {
        return Arrays.stream(SelectionType.values()).map(SelectionType::name).toList();
    }
}
