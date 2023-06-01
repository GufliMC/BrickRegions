package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileRegion;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.brick.regions.api.selection.SelectionType;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Input;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.build.FailureHandler;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.safe.SafeCommandContext;
import com.guflimc.colonel.common.safe.SafeCommandParameterCompleter;
import com.guflimc.colonel.common.safe.SafeCommandParameterParser;
import net.kyori.adventure.audience.Audience;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RegionArguments<S> {

    private final Colonel<S> colonel;

    public RegionArguments(Colonel<S> colonel) {
        this.colonel = colonel;

        colonel.registry().registerParameterParser(Object.class, "attributeValue", this::attributeValueParser);
        colonel.registry().registerParameterCompleter(Object.class, "attributeValue", this::attributeValueCompleter);
    }

    // ATTRIBUTE VALUES

    public Object attributeValueParser(SafeCommandContext<S> ctx, String input) throws Throwable {
        RegionAttributeKey<?> key = ctx.argument("attributeKey");
        SafeCommandParameterParser<S> parser = colonel.registry()
                .parser(key.type())
                .orElse(null);
        if (parser == null) throw new RuntimeException(String
                .format("No value parser for attribute %s with type %s", key.name(), key.type().getSimpleName()));
        return parser.parse(ctx, input);
    }

    public List<Suggestion> attributeValueCompleter(SafeCommandContext<S> ctx, String input) throws Throwable {
        RegionAttributeKey<?> key = ctx.argument("attributeKey");
        if (key == null) return List.of();
        SafeCommandParameterCompleter<S> completer = colonel.registry()
                .completer(key.type())
                .orElse(null);
        if (completer == null) return List.of();
        return completer.suggestions(ctx, input);
    }

    // ATTRIBUTE KEYS

    @Parser("attributeKey")
    public RegionAttributeKey<?> attributeKeyParser(@Source Audience sender, @Input String input) {
        return Optional.ofNullable(RegionAttributeKey.valueOf(input))
                .filter(a -> !a.hidden())
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(sender, "cmd.error.args.attribute", input)));
    }

    @Completer("attributeKey")
    public List<String> attributeKeyCompleter() {
        return Arrays.stream(RegionAttributeKey.values())
                .filter(a -> !a.hidden())
                .map(AttributeKey::name)
                .toList();
    }

    // REGION

    @Parser(type = Region.Keyed.class)
    public Region.Keyed region(@Source Audience audience, @Source("worldId") UUID worldId, @Input String input) {
        return RegionAPI.get()
                .region(worldId, input)
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.Keyed.class)
    public List<String> region(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId, Region.Keyed.class)
                .stream().map(Region.Keyed::name).toList();
    }

    // TILE REGION

    @Completer(value = "region", type = TileRegion.class)
    public List<String> tileRegion(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId, Region.Keyed.class, TileRegion.class).stream()
                .map(Region.Keyed::name).toList();
    }

    @Parser("region")
    public TileRegion tileRegion(@Source Audience audience, @Source("worldId") UUID worldId, @Input String input) {
        return RegionAPI.get()
                .region(worldId, input)
                .filter(TileRegion.class::isInstance)
                .map(TileRegion.class::cast)
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    // REGION NOT GLOBAL

    @Parser(type = Region.Keyed.class, value = "not-global")
    public Region.Keyed regionNotGlobal(@Source Audience audience, @Source("worldId") UUID worldId, @Input String input) {
        return RegionAPI.get()
                .region(worldId, input)
                .filter(region -> !(region instanceof Region.World))
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.Keyed.class, value = "not-global")
    public List<String> regionNotGlobal(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId, Region.Keyed.class)
                .stream()
                .filter(region -> !(region instanceof Region.World))
                .map(Region.Keyed::name).toList();
    }

    // REGION ATTRIBUTEABLE

    @Parser(type = Region.Keyed.class, value = "attributeable")
    public Region.Keyed regionAttributeable(@Source Audience audience, @Source("worldId") UUID worldId, @Input String input) {
        return RegionAPI.get()
                .region(worldId, input)
                .filter(region -> region instanceof Region.AttributeModifiable)
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.Keyed.class, value = "attributeable")
    public List<String> regionAttributeable(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId, Region.Keyed.class)
                .stream()
                .filter(region -> region instanceof Region.AttributeModifiable)
                .map(Region.Keyed::name).toList();
    }

    // REGION RULEABLE

    @Parser(type = Region.Keyed.class, value = "ruleable")
    public Region.Keyed regionRuleable(@Source Audience audience, @Source("worldId") UUID worldId, @Input String input) {
        return RegionAPI.get()
                .region(worldId, input)
                .filter(region -> region instanceof Region.RuleModifiable)
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.region", input)));
    }

    @Completer(type = Region.Keyed.class, value = "ruleable")
    public List<String> regionRuleable(@Source("worldId") UUID worldId) {
        return RegionAPI.get()
                .regions(worldId, Region.Keyed.class)
                .stream()
                .filter(region -> region instanceof Region.RuleModifiable)
                .map(Region.Keyed::name).toList();
    }

    // ATTRIBUTE

    @Completer(type = RegionAttributeKey.class)
    public List<String> attribute() {
        return Arrays.stream(RegionAttributeKey.values())
                .map(RegionAttributeKey::name)
                .toList();
    }

    @Parser(type = RegionAttributeKey.class)
    public RegionAttributeKey<?> attribute(@Source Audience sender, @Input String input) {
        RegionAttributeKey<?> attribute = RegionAttributeKey.valueOf(input);
        if (attribute == null) {
            throw FailureHandler.of(() -> I18nAPI.get(this).send(sender, "cmd.error.args.attribute", input));
        }
        return attribute;
    }

    // RULE TYPE

    @Parser(type = RuleType.class)
    public RuleType ruleType(@Source Audience audience, @Input String input) {
        return Optional.ofNullable(RuleType.valueOf(input.toUpperCase()))
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.ruletype", input)));
    }

    @Completer(type = RuleType.class)
    public List<String> ruleType() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    // RULE TARGET

    @Parser(type = RuleTarget.class)
    public RuleTarget ruleTarget(@Source Audience audience, @Input String input) {
        return Optional.ofNullable(RuleTarget.valueOf(input.toUpperCase()))
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.ruletarget", input)));
    }

    @Completer(type = RuleTarget.class)
    public List<String> ruleTarget() {
        return Arrays.stream(RuleType.values()).map(RuleType::name).toList();
    }

    // RULE STATUS

    @Parser(type = RuleStatus.class)
    public RuleStatus ruleStatus(@Source Audience audience, @Input String input) {
        return Arrays.stream(RuleStatus.values()).filter(rs -> rs.name().equalsIgnoreCase(input)).findFirst()
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.rulestatus", input)));
    }

    @Completer(type = RuleStatus.class)
    public List<String> ruleStatus() {
        return Arrays.stream(RuleStatus.values()).map(RuleStatus::name).toList();
    }

    // SELECTION TYPE

    @Parser(type = SelectionType.class)
    public SelectionType selectionType(@Source Audience audience, @Input String input) {
        return Arrays.stream(SelectionType.values()).filter(st -> st.name().equalsIgnoreCase(input)).findFirst()
                .orElseThrow(() -> FailureHandler.of(() -> I18nAPI.get(this).send(audience, "cmd.error.args.selectiontype", input)));
    }

    @Completer(type = SelectionType.class)
    public List<String> selectionType() {
        return Arrays.stream(SelectionType.values()).map(SelectionType::name).toList();
    }

}
