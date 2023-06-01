package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.attribute.RegionAttributeKey;
import com.guflimc.brick.regions.api.domain.tile.TileGroup;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;

public class RegionAttributeCommands {

    // REGIONS

    @Command("br region attribute set")
    @Permission("brickregions.region.attribute.set")
    public <T, R extends Region.Keyed & Region.AttributeModifiable> void regionAttributeSet(@Source Audience sender,
                                                                                            @Parameter(parser = "attributeable", completer = "attributeable") R region,
                                                                                            @Parameter RegionAttributeKey<T> attributeKey,
                                                                                            @Parameter T attributeValue) {
        region.setAttribute(attributeKey, attributeValue);
        RegionAPI.get().persist(region);

        I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.region.attribute.set",
                attributeKey.name(), attributeValue.toString(), region.name());
    }

    @Command("br region attribute unset")
    @Permission("brickregions.region.attribute.unset")
    public <T, R extends Region.Keyed & Region.AttributeModifiable> void regiosAttributeUnset(@Source Audience sender,
                                                                                              @Parameter(parser = "attributeable", completer = "attributeable") R region,
                                                                                              @Parameter RegionAttributeKey<?> attributeKey) {
        region.removeAttribute(attributeKey);
        RegionAPI.get().persist(region);

        I18nAPI.get(RegionAttributeCommands.class)
                .send(sender, "cmd.region.attribute.unset", attributeKey.name(), region.name());
    }

    // TILE GROUPS

    @Command("br tilegroup attribute set")
    @Permission("brickregions.tilegroup.attributes.set")
    public <T, R extends TileGroup & Region.AttributeModifiable> void tilegroupAttributeSet(@Source Audience sender,
                                                                                            @Source R tileGroup,
                                                                                            @Parameter RegionAttributeKey<T> attributeKey,
                                                                                            @Parameter T attributeValue) {
        tileGroup.setAttribute(attributeKey, attributeValue);
        RegionAPI.get().persist(tileGroup);

        I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.tilegroup.attributes.set",
                attributeKey.name(), attributeValue.toString());
    }

    @Command("br tilegroup attribute unset")
    @Permission("brickregions.tilegroup.attribute.unset")
    public <T, R extends TileGroup & Region.Keyed & Region.AttributeModifiable> void tilegroupAttributeUnset(@Source Audience sender,
                                                                                                             @Source R tileGroup,
                                                                                                             @Parameter RegionAttributeKey<?> attributeKey) {
        tileGroup.removeAttribute(attributeKey);
        RegionAPI.get().persist(tileGroup);

        I18nAPI.get(RegionAttributeCommands.class)
                .send(sender, "cmd.tilegroup.attribute.unset", attributeKey.name());
    }

}
