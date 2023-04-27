package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.i18n.api.I18nAPI;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.LocalityProtectionRule;
import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.domain.WorldRegion;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableProtectedLocality;
import com.guflimc.brick.regions.api.domain.modifiable.ModifiableRegion;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import com.guflimc.brick.regions.api.rules.RuleType;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.minecraft.common.annotations.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@CommandContainer
public class RegionCommands {

    @Command("br regions delete")
    @Permission("brick.regions.delete")
    public void delete(@Source Audience sender, Region region) {
        if (region instanceof WorldRegion) {
            I18nAPI.get(this).send(sender, "cmd.regions.delete.error.global");
        }

        RegionAPI.get().delete(region);
        I18nAPI.get(this).send(sender, "cmd.region.delete", region.name());
    }

    @Command("br regions setdisplayname")
    @Permission("brick.regions.setdisplayname")
    public void setdisplayname(@Source Audience sender,
                               @Parameter ModifiableRegion region,
                               @Parameter(read = CommandParameter.ReadMode.GREEDY) String name) {
        region.setDisplayName(MiniMessage.miniMessage().deserialize(name));
        RegionAPI.get().save(region);
        I18nAPI.get(this).send(sender, "cmd.regions.setdisplayname", region.name(), region.displayName());
    }

    @Command("br regions rules list")
    @Permission("brick.regions.rules.list")
    public <T extends Region & ModifiableProtectedLocality> void rulesList(@Source Audience sender,
                                                                           @Parameter T region) {
        List<LocalityProtectionRule> rules = region.rules();
        if (rules.isEmpty()) {
            I18nAPI.get(this).send(sender, "cmd.regions.rules.list.error.empty", region.name());
            return;
        }

        List<Component> result = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            LocalityProtectionRule rule = rules.get(i);
            result.add(I18nAPI.get(this).translate(sender, "cmd.regions.rules.list.format", (i + 1), rule));
        }

        I18nAPI.get(this).send(sender, "cmd.regions.rules.list", region.name(),
                Component.newline().append(Component.join(JoinConfiguration.newlines(), result)));
    }

    @Command("br regions rules add")
    @Permission("brick.regions.rules.add")
    public <T extends Region & ModifiableProtectedLocality> void rulesAdd(@Source Audience sender,
                                                                          @Parameter T region,
                                                                          @Parameter RuleStatus status,
                                                                          @Parameter RuleTarget target,
                                                                          @Parameter RuleType type) {
        LocalityProtectionRule rule = region.rules().stream()
                .filter(r -> r.status().equals(status))
                .filter(r -> r.target().equals(target))
                .filter(r -> Arrays.asList(r.types()).contains(type))
                .findFirst().orElse(null);

        if (rule != null) {
            I18nAPI.get(this).send(sender, "cmd.regions.rules.add.error.exists");
            return;
        }

        rule = region.addProtectionRule(status, target, type);
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.regions.rules.add", rule, region.name());
    }

    @Command("br regions rules remove")
    @Permission("brick.regions.rules.remove")
    public <T extends Region & ModifiableProtectedLocality> void rulesRemove(@Source Audience sender,
                                                                             @Parameter T region,
                                                                             @Parameter int index) {
        if (region.rules().size() < index || index < 1) {
            I18nAPI.get(this).send(sender, "cmd.regions.rules.remove.error.index");
            return;
        }

        LocalityProtectionRule rule = region.rules().get(index - 1);
        region.removeRule(rule);
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.regions.rules.remove", rule, region.name());
    }

    @Command("br regions rules clear")
    @Permission("brick.regions.rules.clear")
    public <T extends Region & ModifiableProtectedLocality> void rulesClear(@Source Audience sender,
                                                                            @Parameter T region) {
        region.removeRules();
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.regions.rules.clear", region.name());
    }

}
