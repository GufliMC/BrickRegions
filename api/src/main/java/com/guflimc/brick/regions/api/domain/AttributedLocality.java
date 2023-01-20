package com.guflimc.brick.regions.api.domain;

import java.util.Optional;

public interface AttributedLocality extends Locality {

    <T> Optional<T> attribute(LocalityAttributeKey<T> key);

}
