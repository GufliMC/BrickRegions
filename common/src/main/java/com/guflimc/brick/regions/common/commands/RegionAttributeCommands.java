package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.colonel.annotation.AnnotationColonel;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.safe.SafeCommandHandlerBuilder;
import com.guflimc.colonel.common.safe.SafeCommandParameterBuilder;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;

public class RegionAttributeCommands {

    private RegionAttributeCommands() {
    }

    //

    public static <S> void register(AnnotationColonel<S> colonel) {
        colonel.registerAll(new RegionAttributeCommands());

        for (LocalityAttributeKey<?> attribute : LocalityAttributeKey.values()) {
            register(colonel, attribute);
        }
    }

    private static <S, T> void register(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        try {
            // TODO permissions
            registerForRegions(colonel, attributeKey);
            registerForTiles(colonel, attributeKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <S, T> void value(SafeCommandHandlerBuilder<S> builder, LocalityAttributeKey<T> attributeKey) {
        SafeCommandParameterBuilder<S> pb = builder.parameter("value").parser(attributeKey.type()).completer(attributeKey.type());
        if (attributeKey.type().equals(String.class)) {
            pb.readGreedy();
        }
        pb.done();
    }

    private static <S, T> void registerForRegions(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        SafeCommandHandlerBuilder<S> b = colonel.builder();

        b.path("br regions attributes set ");
        b.parameter("region").parser(Region.class).completer(Region.class).done();
        b.literal(attributeKey.name());
        value(b, attributeKey);

        b.source(Audience.class);

        b.executor(ctx -> {
            Region region = ctx.argument("region");
            ModifiableAttributedLocality mr = ctx.argument("region");
            T value = ctx.argument("value");

            mr.setAttribute(attributeKey, value);
            RegionAPI.get().save(mr);

            Audience sender = ctx.source(0);
            I18nAPI.get(RegionAttributeCommands.class)
                    .send(sender, "cmd.regions.attributes.set", region.name(), attributeKey.name(), value.toString());
        });
        b.register();
    }

    private static <S, T> void registerForTiles(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        SafeCommandHandlerBuilder<S> b = colonel.builder();

        b.path("br tiles attributes set " + attributeKey.name());
        value(b, attributeKey);

        b.source(Audience.class);
        b.source(TileGroup.class);

        b.executor(ctx -> {
            ModifiableAttributedLocality tile = ctx.source(1);
            T value = ctx.argument("value");

            tile.setAttribute(attributeKey, value);
            RegionAPI.get().save(tile);

            Audience sender = ctx.source(0);
            I18nAPI.get(RegionAttributeCommands.class)
                    .send(sender, "cmd.tiles.attributes.set", attributeKey.name(), value.toString());
        });
        b.register();
    }

    //

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
