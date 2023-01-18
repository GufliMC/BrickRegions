package com.guflimc.brick.regions.api.domain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Region extends Locality {

    String name();

    default Component displayName() {
        return Component.text(name(), NamedTextColor.WHITE);
    }

}
