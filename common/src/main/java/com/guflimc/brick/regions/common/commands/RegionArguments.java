package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.build.HandleFailure;
import net.kyori.adventure.audience.Audience;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RegionArguments {

    // REGION

    @Parser()
    public Region region(@Source Audience audience, @Source("worldId") UUID worldId, String input) {
        return RegionAPI.get()
                .findRegion(worldId, input)
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.class)
    public List<String> region(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId)
                .stream().map(Region::name).toList();
    }

    // REGION NOT GLOBAL

    @Parser(type = Region.class, value = "not-global")
    public Region regionNotGlobal(@Source Audience audience, @Source("worldId") UUID worldId, String input) {
        return RegionAPI.get()
                .findRegion(worldId, input)
                .filter(region -> !(region instanceof WorldRegion))
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.class, value = "not-global")
    public List<String> regionNotGlobal(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId)
                .stream()
                .filter(region -> !(region instanceof WorldRegion))
                .map(Region::name).toList();
    }

    // ATTRIBUTE

    @Completer(type = LocalityAttributeKey.class)
    public List<String> attribute() {
        return Arrays.stream(LocalityAttributeKey.values())
                .map(LocalityAttributeKey::name)
                .toList();
    }

    @Parser(type = LocalityAttributeKey.class)
    public LocalityAttributeKey<?> attribute(@Source Audience sender, String input) {
        LocalityAttributeKey<?> attribute = LocalityAttributeKey.valueOf(input);
        if ( attribute == null ) {
            throw HandleFailure.of(() -> I18nAPI.get(this).send(sender, "cmd.error.args.attribute", input));
        }
        return attribute;
    }

    // RULE TYPE

    @Parser(type = RuleType.class)
    public RuleType ruleType(@Source Audience audience, String input) {
        return Optional.ofNullable(RuleType.valueOf(input.toUpperCase()))
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.ruletype", input)));
    }

    @Completer(type = RuleType.class)
    public List<String> ruleType() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    // RULE TARGET

    @Parser(type = RuleTarget.class)
    public RuleTarget ruleTarget(@Source Audience audience, String input) {
        return Optional.ofNullable(RuleTarget.valueOf(input.toUpperCase()))
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.ruletarget", input)));
    }

    @Completer(type = RuleTarget.class)
    public List<String> ruleTarget() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    // RULE STATUS

    @Parser(type = RuleStatus.class)
    public RuleStatus ruleStatus(@Source Audience audience, String input) {
        return Arrays.stream(RuleStatus.values()).filter(rs -> rs.name().equalsIgnoreCase(input)).findFirst()
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.rulestatus", input)));
    }

    @Completer(type = RuleStatus.class)
    public List<String> ruleStatus() {
        return Arrays.stream(RuleStatus.values()).map(RuleStatus::name).toList();
    }

    // SELECTION TYPE

    @Parser(type = SelectionType.class)
    public SelectionType selectionType(@Source Audience audience, String input) {
        return Arrays.stream(SelectionType.values()).filter(st -> st.name().equalsIgnoreCase(input)).findFirst()
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.selectiontype", input)));
    }

    @Completer(type = SelectionType.class)
    public List<String> selectionType() {
        return Arrays.stream(SelectionType.values()).map(SelectionType::name).toList();
    }
}
