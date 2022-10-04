package com.guflimc.brick.regions.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.PersistentRegion;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.RegionRule;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
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
    public void delete(Audience sender, @Argument("region") PersistentRegion region) {
        RegionAPI.get().remove(region);
        I18nAPI.get(this).send(sender, "cmd.region.delete", region.name());
    }

    @CommandMethod("br rules list <region>")
    public void rulesList(Audience sender, @Argument("region") PersistentRegion region) {
        I18nAPI.get(this).send(sender, "cmd.region.rules.list", region.name(), region.rules());
    }

    @CommandMethod("br rules add <region> <status> <target> <type>")
    public void rulesAdd(Audience sender,
                         @Argument("region") PersistentRegion region,
                         @Argument("status") RuleStatus status,
                         @Argument("target") RuleTarget target,
                         @Argument("type") RuleType[] types) {
        RegionRule rule = region.addRule(status, target, types);
        RegionAPI.get().update(region);
        I18nAPI.get(this).send(sender, "cmd.region.rules.add", rule, region.name());
    }

    @CommandMethod("br rules remove <region> <status> <target> <type>")
    public void rulesRemove(Audience sender,
                            @Argument("region") PersistentRegion region,
                            @Argument("status") RuleStatus status,
                            @Argument("target") RuleTarget target,
                            @Argument("type") RuleType[] types) {
        RegionRule rule = region.rules().stream()
                .filter(r -> r.status().equals(status))
                .filter(r -> r.target().equals(target))
                .findFirst().orElse(null); // TODO filter types
        if ( rule == null ) {
            // TODO
            return;
        }

        region.removeRule(rule);
        RegionAPI.get().update(region);

        I18nAPI.get(this).send(sender, "cmd.region.rules.remove", rule, region.name());
    }


}
