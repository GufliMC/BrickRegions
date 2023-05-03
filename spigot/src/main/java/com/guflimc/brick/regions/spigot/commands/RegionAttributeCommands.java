package com.guflimc.brick.regions.spigot.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.ShapeRegion;
import com.guflimc.brick.regions.api.domain.Tile;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableRegion;
import com.guflimc.brick.regions.api.selection.Selection;
import com.guflimc.colonel.annotation.AnnotationColonel;
import com.guflimc.colonel.common.build.CommandHandlerBuilder;
import com.guflimc.colonel.common.build.CommandParameterBuilder;
import net.kyori.adventure.audience.Audience;

import java.util.Collection;

public class RegionAttributeCommands {

    private RegionAttributeCommands() {
    }

    public static <S> void register(AnnotationColonel<S> colonel) {
        for (LocalityAttributeKey<?> attribute : LocalityAttributeKey.values()) {
            register(colonel, attribute);
        }
    }

    private static <S, T> void register(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        try {
            // TODO permissions
            registerForRegions(colonel, attributeKey);
            registerForTiles(colonel, attributeKey);
            registerForSelection(colonel, attributeKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <S, T> void value(CommandHandlerBuilder<S> builder, LocalityAttributeKey<T> attributeKey) {
        CommandParameterBuilder<S> pb = builder.parameter("value").parser(attributeKey.type()).completer(attributeKey.type());
        if (attributeKey.type().equals(String.class)) {
            pb.readGreedy();
        }
        pb.done();
    }

    private static <S, T> void registerForRegions(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        CommandHandlerBuilder<S> b = colonel.builder();

        b.path("br regions attributes set " + attributeKey.name());
        b.parameter("region").parser(Region.class).completer(Region.class).done();
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
        CommandHandlerBuilder<S> b = colonel.builder();

        b.path("br tiles attributes set " + attributeKey.name());
        value(b, attributeKey);

        b.source(Audience.class);
        b.source(Tile.class);

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

    private static <S, T> void registerForSelection(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        CommandHandlerBuilder<S> b = colonel.builder();

        b.path("br attributes set " + attributeKey.name());
        value(b, attributeKey);

        b.source(Audience.class);
        b.source(Selection.class);

        b.executor(ctx -> {
            Selection selection = ctx.source(1);
            T value = ctx.argument("value");

            Collection<ShapeRegion> regions = RegionAPI.get().intersecting(selection.worldId(), selection.shape());
            regions.stream()
                    .filter(r -> r instanceof ModifiableAttributedLocality)
                    .map(r -> (ModifiableAttributedLocality) r)
                    .forEach(r -> r.setAttribute(attributeKey, value));
            RegionAPI.get().save(regions);

            Collection<Tile> tiles = RegionAPI.get().intersecting(selection.worldId(), selection.shape().contour());
            tiles.forEach(t -> t.setAttribute(attributeKey, value));
            RegionAPI.get().save(tiles);

            Audience sender = ctx.source(0);
            I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.attributes.set",
                    attributeKey.name(), value.toString(), regions.size(), tiles.size());
        });
        b.register();
    }

}
