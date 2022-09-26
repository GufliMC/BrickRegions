package com.guflimc.brick.regions.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import net.kyori.adventure.audience.Audience;

import java.util.Objects;

//@CommandContainer
public class RegionCommands {

    @CommandMethod("br list")
    public void list(Audience sender) {
        I18nAPI.get(this).send(sender, "cmd.region.list",
                RegionAPI.get().regions().stream()
                        .map(Region::name)
                        .filter(Objects::nonNull).toList()
        );
    }

    @CommandMethod("br delete <region>")
    public void delete(Audience sender, @Argument(value = "region") Region region) {
        RegionAPI.get().remove(region);
        I18nAPI.get(this).send(sender, "cmd.region.delete", region.name());
    }



}
