package com.guflimc.brick.regions.common.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.meta.CommandMeta;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import net.kyori.adventure.audience.Audience;

public class RegionAttributeCommands {

    private static final LocalityAttributeKey<?>[] KEYS = new LocalityAttributeKey[]{
            LocalityAttributeKey.MAP_HIDDEN,
            LocalityAttributeKey.MAP_CLICK_TEXT,
            LocalityAttributeKey.MAP_HOVER_TEXT,
            LocalityAttributeKey.MAP_FILL_COLOR,
            LocalityAttributeKey.MAP_FILL_OPACITY,
            LocalityAttributeKey.MAP_STROKE_COLOR,
            LocalityAttributeKey.MAP_STROKE_OPACITY,
            LocalityAttributeKey.MAP_STROKE_WEIGHT,
    };

    private RegionAttributeCommands() {
    }

    public static <S> void register(CommandManager<S> commandManager) {
        for (LocalityAttributeKey<?> attribute : KEYS) {
            register(commandManager, attribute);
        }
    }

    private static <T, S> void register(CommandManager<S> commandManager, LocalityAttributeKey<T> attribute) {
        // for regions
        commandManager.command(Command.<S>newBuilder(
                        "attribute_" + attribute.name(), CommandMeta.simple().build())
                .literal("br")
                .literal("attribute")
                .literal(attribute.name())
                .argument(CommandArgument.ofType(Region.class, "region"))
                .argument(CommandArgument.ofType(attribute.type(), "value"))
                .handler(ctx -> {
                    Audience sender = ctx.get("audience");
                    Region region = ctx.get("region");
                    ModifiableAttributedLocality mr = ctx.get("region");
                    T value = ctx.get("value");

                    mr.setAttribute(attribute, value);
                    I18nAPI.get(RegionAttributeCommands.class)
                            .send(sender, "cmd.region.attribute", region.name(), attribute.name(), value.toString());
                })
        );

        // for tiles
        commandManager.command(Command.<S>newBuilder(
                        "attribute_" + attribute.name(), CommandMeta.simple().build())
                .literal("br")
                .literal("tile")
                .literal("attribute")
                .literal(attribute.name())
                .argument(CommandArgument.ofType(attribute.type(), "value"))
                .handler(ctx -> {
                    Audience sender = ctx.get("audience");
                    ModifiableAttributedLocality tile = ctx.get("tile");
                    T value = ctx.get("value");

                    tile.setAttribute(attribute, value);
                    I18nAPI.get(RegionAttributeCommands.class)
                            .send(sender, "cmd.tile.attribute", attribute.name(), value.toString());
                })
        );
    }

}
