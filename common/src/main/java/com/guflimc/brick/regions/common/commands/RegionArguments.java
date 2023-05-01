package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RegionArguments {

    // REGION

    @Parser(type = Region.class)
    public Argument region(@Source Audience audience, @Source("worldId") UUID worldId, String input) {
        return RegionAPI.get()
                .findRegion(worldId, input)
                .map(Argument::success)
                .orElse(Argument.fail(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.class)
    public List<String> region(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId)
                .stream().map(Region::name).toList();
    }

    // ATTRIBUTE

    @Completer(type = LocalityAttributeKey.class)
    public List<String> attribute() {
        return Arrays.stream(LocalityAttributeKey.values())
                .map(LocalityAttributeKey::name)
                .toList();
    }

    @Parser(type = LocalityAttributeKey.class)
    public Argument attribute(@Source Audience sender, String input) {
        LocalityAttributeKey<?> attribute = LocalityAttributeKey.valueOf(input);
        if ( attribute == null ) {
            return Argument.fail(() -> I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.error.args.attribute", input));
        }
        return Argument.success(attribute);
    }

    // RULE TYPE

    @Parser(type = RuleType.class)
    public Argument ruleType(@Source Audience audience, String input) {
        return Optional.ofNullable(RuleType.valueOf(input.toUpperCase()))
                .map(Argument::success)
                .orElse(Argument.fail(() -> I18nAPI.get(this).send(audience, "cmd.error.args.ruletype", input)));
    }

    @Completer(type = RuleType.class)
    public List<String> ruleType() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    // RULE TARGET

    @Parser(type = RuleTarget.class)
    public Argument ruleTarget(@Source Audience audience, String input) {
        return Optional.ofNullable(RuleType.valueOf(input.toUpperCase()))
                .map(Argument::success)
                .orElse(Argument.fail(() -> I18nAPI.get(this).send(audience, "cmd.error.args.ruletarget", input)));
    }

    @Completer(type = RuleTarget.class)
    public List<String> ruleTarget() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    // RULE STATUS

    @Parser(type = RuleStatus.class)
    public Argument ruleStatus(@Source Audience audience, String input) {
        return Arrays.stream(RuleStatus.values()).filter(rs -> rs.name().equalsIgnoreCase(input)).findFirst()
                .map(Argument::success)
                .orElse(Argument.fail(() -> I18nAPI.get(this).send(audience, "cmd.error.args.rulestatus", input)));
    }

    @Completer(type = RuleStatus.class)
    public List<String> ruleStatus() {
        return Arrays.stream(RuleStatus.values()).map(RuleStatus::name).toList();
    }

    // SELECTION TYPE

    @Parser(type = SelectionType.class)
    public Argument selectionType(@Source Audience audience, String input) {
        return Arrays.stream(SelectionType.values()).filter(st -> st.name().equalsIgnoreCase(input)).findFirst()
                .map(Argument::success)
                .orElse(Argument.fail(() -> I18nAPI.get(this).send(audience, "cmd.error.args.selectiontype", input)));
    }

    @Completer(type = SelectionType.class)
    public List<String> selectionType() {
        return Arrays.stream(SelectionType.values()).map(SelectionType::name).toList();
    }
}