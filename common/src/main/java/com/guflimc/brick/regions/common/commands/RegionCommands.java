package com.guflimc.brick.regions.common.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
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
import com.guflimc.brick.regions.common.domain.DShapeRegion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@CommandContainer
public class RegionCommands {

    @CommandMethod("br regions delete <region>")
    @CommandPermission("brick.regions.delete")
    public void delete(Audience sender, @Argument("region") Region region) {
        if (region instanceof WorldRegion) {
            I18nAPI.get(this).send(sender, "cmd.regions.delete.error.global");
        }

        RegionAPI.get().delete(region);
        I18nAPI.get(this).send(sender, "cmd.region.delete", region.name());
    }

    @CommandMethod("br regions setdisplayname <region> <name>")
    @CommandPermission("brick.regions.setdisplayname")
    public void setdisplayname(Audience sender,
                               @Argument(value = "region", parserName = "region") ModifiableRegion region,
                               @Argument("name") @Greedy String name) {
        region.setDisplayName(MiniMessage.miniMessage().deserialize(name));
        RegionAPI.get().save(region);
        I18nAPI.get(this).send(sender, "cmd.regions.setdisplayname", region.name(), region.displayName());
    }

    @CommandMethod("br regions rules list <region>")
    @CommandPermission("brick.regions.rules.list")
    public <T extends Region & ModifiableProtectedLocality> void rulesList(Audience sender,
                                                                           @Argument(value = "region", parserName = "region") T region) {
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

    @CommandMethod("br regions rules add <region> <status> <target> <type>")
    @CommandPermission("brick.regions.rules.add")
    public <T extends Region & ModifiableProtectedLocality> void rulesAdd(Audience sender,
                                                                          @Argument(value = "region", parserName = "region") T region,
                                                                          @Argument("status") RuleStatus status,
                                                                          @Argument("target") RuleTarget target,
                                                                          @Argument("type") RuleType type) {
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

    @CommandMethod("br regions rules remove <region> <index>")
    @CommandPermission("brick.regions.rules.remove")
    public <T extends Region & ModifiableProtectedLocality> void rulesRemove(Audience sender,
                                                                             @Argument(value = "region", parserName = "region") T region,
                                                                             @Argument("index") int index) {
        if (region.rules().size() < index || index < 1) {
            I18nAPI.get(this).send(sender, "cmd.regions.rules.remove.error.index");
            return;
        }

        LocalityProtectionRule rule = region.rules().get(index - 1);
        region.removeRule(rule);
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.regions.rules.remove", rule, region.name());
    }

    @CommandMethod("br regions rules clear <region>")
    @CommandPermission("brick.regions.rules.clear")
    public <T extends Region & ModifiableProtectedLocality> void rulesClear(Audience sender,
                                                                            @Argument(value = "region", parserName = "region") T region) {
        region.removeRules();
        RegionAPI.get().save(region);

        I18nAPI.get(this).send(sender, "cmd.regions.rules.clear", region.name());
    }

}
