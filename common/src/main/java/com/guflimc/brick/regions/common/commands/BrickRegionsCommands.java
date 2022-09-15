package com.guflimc.brick.regions.common.commands;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import net.kyori.adventure.audience.Audience;

import java.util.Objects;

@CommandContainer
public class BrickRegionsCommands {

    @CommandMethod("br list")
    public void list(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.list",
                RegionAPI.get().regions().stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }



}
