package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.Rule;
import com.guflimc.brick.regions.api.rules.attributes.RuleAction;
import com.guflimc.brick.regions.api.rules.attributes.RuleActionType;
import com.guflimc.brick.regions.api.rules.attributes.RuleCondition;
import com.guflimc.brick.regions.api.rules.attributes.RuleStatus;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RegionCommands {

    // MANAGE

    @Command("br region delete")
    @Permission("brickregions.region.delete")
    public void delete(@Source Audience sender, @Parameter(parser = "not-global", completer = "not-global") Region.Keyed region) {
        RegionAPI.get().remove(region);
        I18nAPI.get(this).send(sender, "cmd.region.delete", region.name());
    }

    // RULES

    @Command("br region rule list")
    @Permission("brickregions.region.rule.list")
    public <R extends Region.Keyed & Region.RuleModifiable> void rulesList(@Source Audience sender,
                                                                           @Parameter(parser = "ruleable", completer = "ruleable") R region) {

        List<Rule> rules = region.rules();
        if (rules.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.region.rule.list.error.empty", region.name());
            return;
        }

        List<Component> result = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            result.add(I18nAPI.get(this).translate(sender, "cmd.region.rule.list.format", (i + 1), rule));
        }

        I18nAPI.get(this).send(sender, "cmd.region.rule.list", region.name(),
                Component.newline().append(Component.join(JoinConfiguration.newlines(), result)));
    }

    @Command("br region rule add")
    @Permission("brickregions.region.rule.add")
    public <R extends Region.Keyed & Region.RuleModifiable> void rulesAdd(@Source Audience sender,
                                                                          @Parameter(parser = "ruleable", completer = "ruleable") R region,
                                                                          @Parameter RuleStatus status,
                                                                          @Parameter RuleCondition condition,
                                                                          @Parameter RuleActionType type,
                                                                          @Parameter RuleAction action,
                                                                          @Parameter int priority) {

        Rule rule = region.rules().stream()
                .filter(r -> r.status().equals(status))
                .filter(r -> r.condition().equals(condition))
                .filter(r -> r.action().equals(action))
                .findFirst().orElse(null);

        if (rule != null) {
            I18nAPI.get(this).send(sender, "cmd.region.rule.add.error.exists");
            return;
        }

        rule = region.addRule(status, condition, action, priority);
        RegionAPI.get().persist(region);

        I18nAPI.get(this).send(sender, "cmd.region.rule.add", rule, region.name());
    }

    @Command("br region rule add")
    @Permission("brickregions.region.rule.add")
    public <R extends Region.Keyed & Region.RuleModifiable> void rulesAdd(@Source Audience sender,
                                                                          @Parameter(parser = "ruleable", completer = "ruleable") R region,
                                                                          @Parameter RuleStatus status,
                                                                          @Parameter RuleCondition condition,
                                                                          @Parameter RuleActionType type,
                                                                          @Parameter RuleAction action) {
        rulesAdd(sender, region, status, condition, type, action, 0);
    }

    @Command("br region rule remove")
    @Permission("brickregions.region.rule.remove")
    public <R extends Region.Keyed & Region.RuleModifiable> void rulesRemove(@Source Audience sender,
                                                                             @Parameter(parser = "ruleable", completer = "ruleable") R region,
                                                                             @Parameter int index) {

        if (region.rules().size() < index || index < 1) {
            I18nAPI.get(this).send(sender, "cmd.region.rule.remove.error.index");
            return;
        }

        Rule rule = region.rules().get(index - 1);
        region.removeRule(rule);
        RegionAPI.get().persist(region);

        I18nAPI.get(this).send(sender, "cmd.region.rule.remove", rule, region.name());
    }

    @Command("br region rule clear")
    @Permission("brickregions.region.rule.clear")
    public <R extends Region.Keyed & Region.RuleModifiable> void rulesClear(@Source Audience sender,
                                                                            @Parameter(parser = "ruleable", completer = "ruleable") R region) {
        region.removeRules();
        RegionAPI.get().persist(region);

        I18nAPI.get(this).send(sender, "cmd.region.rule.clear", region.name());
    }

}
