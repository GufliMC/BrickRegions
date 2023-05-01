package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
import com.guflimc.colonel.annotation.AnnotationColonel;
import com.guflimc.colonel.annotation.CommandSourceMapper;
import com.guflimc.colonel.common.build.CommandParameterCompleter;
import com.guflimc.colonel.common.build.CommandParameterParser;
import net.kyori.adventure.audience.Audience;

public class RegionAttributeCommands {

    private RegionAttributeCommands() {
    }

    public static <S> void register(AnnotationColonel<S> colonel) {
        for (LocalityAttributeKey<?> attribute : LocalityAttributeKey.values()) {
            register(colonel, attribute);
        }
    }

    private static <S, T> void register(AnnotationColonel<S> colonel, LocalityAttributeKey<T> attributeKey) {
        CommandSourceMapper<S> mapper = colonel.registry().mapper(Audience.class).orElseThrow();

        CommandParameterParser<S> parser = colonel.registry().parser(attributeKey.type()).orElse(null);
        if ( parser == null ) {
            return; // can't register
        }

        CommandParameterCompleter<S> completer = colonel.registry().completer(attributeKey.type()).orElse(null);

        colonel.register("br regions attributes set " + attributeKey.name(), b -> {
            b.string("region", colonel.registry().parser(Region.class).orElseThrow(), colonel.registry().completer(Region.class).orElseThrow());

            if ( completer == null ) {
                b.string("value", parser);
            } else {
                b.string("value", parser, completer);
            }

            b.executor(ctx -> {
                Region region = ctx.argument("region");
                ModifiableAttributedLocality mr = ctx.argument("region");
                T value = ctx.argument("value");

                mr.setAttribute(attributeKey, value);
                RegionAPI.get().save(mr);

                Audience sender = (Audience) mapper.map(ctx.source());
                I18nAPI.get(RegionAttributeCommands.class)
                            .send(sender, "cmd.regions.attribute.set", attributeKey.name(), value.toString(), region.name());
            });
        });
    }

}
