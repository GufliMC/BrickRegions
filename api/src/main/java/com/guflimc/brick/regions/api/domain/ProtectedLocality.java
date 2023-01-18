package com.guflimc.brick.regions.api.domain;

import java.util.List;

public interface ProtectedLocality extends Locality {

    List<LocalityProtectionRule> rules();

}
