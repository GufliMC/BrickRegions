package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableAttributedLocality;
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
    public <T> void regionsAttributesSet(@Source Audience sender,
                                         @Parameter Region region,
                                         @Parameter LocalityAttributeKey<T> attributeKey,
                                         @Parameter T attributeValue) {
        ModifiableAttributedLocality mr = (ModifiableAttributedLocality) region;

        mr.setAttribute(attributeKey, attributeValue);
        RegionAPI.get().save(mr);

        I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.region.attribute.set",
                attributeKey.name(), attributeValue.toString(), region.name());
    }

    @Command("br region attribute unset")
    @Permission("brickregions.region.attribute.unset")
    public <T extends Region & ModifiableAttributedLocality> void regionsAttributesUnset(@Source Audience sender,
                                                                                         @Parameter T region,
                                                                                         @Parameter LocalityAttributeKey<?> attributeKey) {
        region.removeAttribute(attributeKey);
        RegionAPI.get().save(region);

        I18nAPI.get(RegionAttributeCommands.class)
                .send(sender, "cmd.region.attribute.unset", attributeKey.name(), region.name());
    }

    // TILE GROUPS

    @Command("br tilegroup attribute set")
    @Permission("brickregions.tilegroup.attributes.set")
    public <T> void tilesAttributesSet(@Source Audience sender,
                                       @Source TileGroup tileGroup,
                                       @Parameter LocalityAttributeKey<T> attributeKey,
                                       @Parameter T attributeValue) {
        tileGroup.setAttribute(attributeKey, attributeValue);
        RegionAPI.get().save(tileGroup);

        I18nAPI.get(RegionAttributeCommands.class).send(sender, "cmd.tilegroup.attributes.set",
                attributeKey.name(), attributeValue.toString());
    }

    @Command("br tilegroup attribute unset")
    @Permission("brickregions.tilegroup.attribute.unset")
    public void tilesAttributesUnset(@Source Audience sender,
                                     @Source TileGroup tileGroup,
                                     @Parameter LocalityAttributeKey<?> attributeKey) {
        tileGroup.removeAttribute(attributeKey);
        RegionAPI.get().save(tileGroup);

        I18nAPI.get(RegionAttributeCommands.class)
                .send(sender, "cmd.tilegroup.attribute.unset", attributeKey.name());
    }

}
