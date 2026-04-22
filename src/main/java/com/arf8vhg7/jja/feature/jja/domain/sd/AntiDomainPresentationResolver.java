package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;

final class AntiDomainPresentationResolver {
    private AntiDomainPresentationResolver() {
    }

    static AntiDomainPresentation resolveLastActivated(
        AntiDomainPresentation runtimePresentation,
        AntiDomainPresentation latchedPresentation,
        AntiDomainTechniqueOption pressedOptionAtPress
    ) {
        if (runtimePresentation != null && runtimePresentation != AntiDomainPresentation.NONE) {
            return runtimePresentation;
        }
        if (latchedPresentation != null && latchedPresentation != AntiDomainPresentation.NONE) {
            return latchedPresentation;
        }
        AntiDomainPresentation pressedPresentation = resolveSelection(pressedOptionAtPress);
        return pressedPresentation != AntiDomainPresentation.NONE ? pressedPresentation : AntiDomainPresentation.SIMPLE_DOMAIN;
    }

    static AntiDomainPresentation resolveTechniqueSetupPresentation(
        AntiDomainPresentation runtimePresentation,
        AntiDomainPresentation latchedPresentation,
        AntiDomainTechniqueOption pressedOptionAtPress,
        boolean ownsSimpleDomain,
        AntiDomainTechniqueOption currentSelection
    ) {
        AntiDomainPresentation activePresentation = resolveActive(
            runtimePresentation,
            latchedPresentation,
            pressedOptionAtPress,
            ownsSimpleDomain
        );
        return activePresentation != AntiDomainPresentation.NONE ? activePresentation : resolveSelection(currentSelection);
    }

    static AntiDomainPresentation resolveActive(
        AntiDomainPresentation runtimePresentation,
        AntiDomainPresentation latchedPresentation,
        AntiDomainTechniqueOption pressedOptionAtPress,
        boolean ownsSimpleDomain
    ) {
        if (runtimePresentation != null && runtimePresentation != AntiDomainPresentation.NONE) {
            return runtimePresentation;
        }
        if (latchedPresentation != null && latchedPresentation != AntiDomainPresentation.NONE) {
            return latchedPresentation;
        }
        AntiDomainPresentation pressedPresentation = resolveSelection(pressedOptionAtPress);
        if (pressedPresentation != AntiDomainPresentation.NONE) {
            return pressedPresentation;
        }
        return ownsSimpleDomain ? AntiDomainPresentation.SIMPLE_DOMAIN : AntiDomainPresentation.NONE;
    }

    static AntiDomainPresentation resolveSelection(AntiDomainTechniqueOption option) {
        if (option == AntiDomainTechniqueOption.HOLLOW_WICKER_BASKET) {
            return AntiDomainPresentation.HOLLOW_WICKER_BASKET;
        }
        if (option == AntiDomainTechniqueOption.SIMPLE_DOMAIN) {
            return AntiDomainPresentation.SIMPLE_DOMAIN;
        }
        return AntiDomainPresentation.NONE;
    }
}
