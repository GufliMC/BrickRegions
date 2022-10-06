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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        List<RegionRule> rules = region.rules();
        if ( rules.isEmpty() ) {
            I18nAPI.get(this).send(sender, "cmd.region.rules.list.error.empty", region.name());
            return;
        }

        List<Component> result = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            RegionRule rule = rules.get(i);
            result.add(I18nAPI.get(this).translate(sender, "cmd.region.rules.list.format", (i + 1), rule));
        }

        I18nAPI.get(this).send(sender, "cmd.region.rules.list", region.name(),
                Component.newline().append(Component.join(JoinConfiguration.newlines(), result)));
    }

    @CommandMethod("br rules add <region> <status> <target> <type>")
    public void rulesAdd(Audience sender,
                         @Argument("region") PersistentRegion region,
                         @Argument("status") RuleStatus status,
                         @Argument("target") RuleTarget target,
                         @Argument("type") RuleType type) {
        RegionRule rule = region.rules().stream()
                .filter(r -> r.status().equals(status))
                .filter(r -> r.target().equals(target))
                .filter(r -> Arrays.asList(r.types()).contains(type))
                .findFirst().orElse(null);

        if ( rule != null ) {
            I18nAPI.get(this).send(sender, "cmd.region.rules.add.error.exists");
            return;
        }

        rule = region.addRule(status, target, type);
        RegionAPI.get().update(region);

        I18nAPI.get(this).send(sender, "cmd.region.rules.add", rule, region.name());
    }

    @CommandMethod("br rules remove <region> <index>")
    public void rulesRemove(Audience sender,
                            @Argument("region") PersistentRegion region,
                            @Argument("index") int index) {
        if ( region.rules().size() < index || index < 1 ) {
            I18nAPI.get(this).send(sender, "cmd.region.rules.remove.error.index");
            return;
        }

        RegionRule rule = region.rules().get(index-1);
        region.removeRule(rule);
        RegionAPI.get().update(region);

        I18nAPI.get(this).send(sender, "cmd.region.rules.remove", rule, region.name());
    }

    @CommandMethod("br rules clear <region>")
    public void rulesClear(Audience sender,
                            @Argument("region") PersistentRegion region) {

        region.clearRules();
        RegionAPI.get().update(region);

        I18nAPI.get(this).send(sender, "cmd.region.rules.clear", region.name());
    }


}
