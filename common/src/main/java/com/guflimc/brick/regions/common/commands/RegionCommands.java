package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.locality.LocalityRule;
import com.guflimc.brick.regions.api.domain.region.ModifiableRegion;
import com.guflimc.brick.regions.api.domain.region.Region;
import com.guflimc.brick.regions.api.domain.region.WorldRegion;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegionCommands {

    // MANAGE

    @Command("br region delete")
    @Permission("brickregions.region.delete")
    public void delete(@Source Audience sender, @Parameter(parser = "not-global", completer = "not-global") Region region) {
        if (region instanceof WorldRegion) {
            I18nAPI.get(this).send(sender, "cmd.region.delete.error.global");
        }

        RegionAPI.get().delete(region);
        I18nAPI.get(this).send(sender, "cmd.region.delete", region.key().name());
    }

    // RULES

    @Command("br region rule list")
    @Permission("brickregions.region.rule.list")
    public void rulesList(@Source Audience sender,
                          @Parameter ModifiableRegion region) {

        List<LocalityRule> rules = region.rules();
        if (rules.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.region.rule.list.error.empty", region.key().name());
            return;
        }

        List<Component> result = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            LocalityRule rule = rules.get(i);
            result.add(I18nAPI.get(this).translate(sender, "cmd.region.rule.list.format", (i + 1), rule));
        }

        I18nAPI.get(this).send(sender, "cmd.region.rule.list", region.key().name(),
                Component.newline().append(Component.join(JoinConfiguration.newlines(), result)));
    }

    @Command("br region rule add")
    @Permission("brickregions.region.rule.add")
    public void rulesAdd(@Source Audience sender,
                         @Parameter ModifiableRegion region,
                         @Parameter RuleStatus status,
                         @Parameter RuleTarget target,
                         @Parameter RuleType type) {

        LocalityRule rule = region.rules().stream()
                .filter(r -> r.status().equals(status))
                .filter(r -> r.target().equals(target))
                .filter(r -> Arrays.asList(r.types()).contains(type))
                .findFirst().orElse(null);

        if (rule != null) {
            I18nAPI.get(this).send(sender, "cmd.region.rule.add.error.exists");
            return;
        }

        rule = region.addRule(status, target, type);
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.region.rule.add", rule, region.key().name());
    }

    @Command("br region rule remove")
    @Permission("brickregions.region.rule.remove")
    public void rulesRemove(@Source Audience sender,
                            @Parameter ModifiableRegion region,
                            @Parameter int index) {

        if (region.rules().size() < index || index < 1) {
            I18nAPI.get(this).send(sender, "cmd.region.rule.remove.error.index");
            return;
        }

        LocalityRule rule = region.rules().get(index - 1);
        region.removeRule(rule);
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.region.rule.remove", rule, region.key().name());
    }

    @Command("br region rule clear")
    @Permission("brickregions.region.rule.clear")
    public void rulesClear(@Source Audience sender,
                           @Parameter ModifiableRegion region) {
        region.removeRules();
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.region.rule.clear", region.key().name());
    }

}
