package com.arf8vhg7.jja.feature.jja.technique.shared.registration;

import java.util.Objects;

public record TechniqueSetupRegistrationCandidate(String displayName, int selectTechniqueId, String canonicalName) {
    public TechniqueSetupRegistrationCandidate {
        displayName = Objects.requireNonNull(displayName, "displayName");
        canonicalName = Objects.requireNonNull(canonicalName, "canonicalName");
    }
}