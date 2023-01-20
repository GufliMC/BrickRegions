package com.guflimc.brick.regions.api.domain.modifiable;

import com.guflimc.brick.regions.api.domain.AttributedLocality;
import com.guflimc.brick.regions.api.domain.Locality;
import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;

public interface ModifiableAttributedLocality extends ModifiableLocality, AttributedLocality {

    <T> void setAttribute(LocalityAttributeKey<T> key, T value);

    <T> void removeAttribute(LocalityAttributeKey<T> key);

}

