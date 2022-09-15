package com.guflimc.brick.regions.spigot.api.rules;

import com.guflimc.brick.regions.api.domain.Region;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.function.BiPredicate;

public class SpigotRuleTarget extends RuleTarget<Player> {

    public static final SpigotRuleTarget ANY = new SpigotRuleTarget("ANY", (p, r) -> true);

    public static final SpigotRuleTarget CREATIVE = new SpigotRuleTarget("CREATIVE",
            (p, r) -> p.getGameMode().equals(GameMode.CREATIVE));

    public static final SpigotRuleTarget SURVIVAL = new SpigotRuleTarget("SURVIVAL",
            (p, r) -> p.getGameMode().equals(GameMode.SURVIVAL));

    //

    public SpigotRuleTarget(String name, BiPredicate<Player, Region> predicate) {
        super(name, predicate);
    }

}
