package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.orm.api.attributes.AttributeKey;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.build.HandleFailure;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.safe.SafeCommandContext;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RegionAttributeCommands<S> {

    private final Colonel<S> colonel;

    public RegionAttributeCommands(Colonel<S> colonel) {
        this.colonel = colonel;
    }

    //

    @Parser("attributeKey")
    public LocalityAttributeKey<?> attributeKeyParser(@Source Audience sender, String input) {
        return Optional.ofNullable(LocalityAttributeKey.valueOf(input))
                .filter(a -> !a.hidden())
                .orElseThrow(() -> HandleFailure.of(() -> I18nAPI.get(this).send(sender, "cmd.error.args.attribute", input)));
    }

    @Completer("attributeKey")
    public List<String> attributeKeyCompleter() {
        return Arrays.stream(LocalityAttributeKey.values())
                .filter(a -> !a.hidden())
                .map(AttributeKey::name)
                .toList();
    }

    @Parser("attributeValue")
    public Object attributeValueParser(SafeCommandContext<S> ctx, String input) {
        LocalityAttributeKey<?> key = ctx.argument("attributeKey");
        return colonel.registry().parser(key.type())
                .map(parser -> parser.parse(ctx, input))
                .orElseThrow(() -> HandleFailure.of(String.format("No value parser for attribute %s with type %s", key.name(), key.type().getSimpleName())));
    }

    @Completer("attributeValue")
    public List<Suggestion> attributeValueCompleter(SafeCommandContext<S> ctx, String input) {
        LocalityAttributeKey<?> key = ctx.argument("attributeKey");
        return colonel.registry().completer(key.type())
                .map(completer -> completer.suggestions(ctx, input))
                .orElse(List.of());
    }

    //

    @Command("br regions attributes set ")
    @Permission("brickregions.regions.attributes.set")
    public <T> void regionsAttributesSet(@Source Audience sender,
                                         @Parameter Region region,
                                         @Parameter LocalityAttributeKey<T> attributeKey,
                                         @Parameter T attributeValue) {
        ModifiableAttributedLocality mr = (ModifiableAttributedLocality) region;

        mr.setAttribute(attributeKey, attributeValue);
        RegionAPI.get().save(mr);

        I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.regions.attributes.set",
                region.name(), attributeKey.name(), attributeValue.toString());
    }


    @Command("br regions attributes unset ")
    @Permission("brickregions.regions.tiles.attributes.unset")
    public <T extends Region & ModifiableAttributedLocality> void regionsAttributesUnset(@Source Audience sender,
                                                                                         @Parameter T region,
                                                                                         @Parameter LocalityAttributeKey<?> attributeKey) {
        region.removeAttribute(attributeKey);
        RegionAPI.get().save(region);

        I18nAPI.get(RegionAttributeCommands.class)
                .send(sender, "cmd.tiles.attributes.unset", attributeKey.name());
    }

    @Command("br tiles attributes set ")
    @Permission("brickregions.tiles.attributes.set")
    public <T> void tilesAttributesSet(@Source Audience sender,
                                       @Source TileGroup tileGroup,
                                       @Parameter LocalityAttributeKey<T> attributeKey,
                                       @Parameter T attributeValue) {
        tileGroup.setAttribute(attributeKey, attributeValue);
        RegionAPI.get().save(tileGroup);

        I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.tiles.attributes.set",
                attributeKey.name(), attributeValue.toString());
    }

    @Command("br tiles attributes unset ")
    @Permission("brickregions.tiles.attributes.unset")
    public void tilesAttributesUnset(@Source Audience sender,
                                     @Source TileGroup tileGroup,
                                     @Parameter LocalityAttributeKey<?> attributeKey) {
        tileGroup.removeAttribute(attributeKey);
        RegionAPI.get().save(tileGroup);

        I18nAPI.get(RegionAttributeCommands.class)
                .send(sender, "cmd.tiles.attributes.unset", attributeKey.name());
    }

}
