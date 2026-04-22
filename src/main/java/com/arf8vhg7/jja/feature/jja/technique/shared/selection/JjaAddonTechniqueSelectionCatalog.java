package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import com.arf8vhg7.jja.feature.jja.technique.family.kashimo.KashimoTechniqueSelectionService;
import com.arf8vhg7.jja.feature.jja.technique.family.kugisaki.KugisakiTechniqueSelectionService;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowTechniqueSelectionService;

public final class JjaAddonTechniqueSelectionCatalog {
    private JjaAddonTechniqueSelectionCatalog() {
    }

    public static TechniqueSelectionCandidate resolveCandidate(int curseTechniqueId, int selectTechniqueId) {
        if (curseTechniqueId == KashimoTechniqueSelectionService.CURSE_TECHNIQUE_ID
            && selectTechniqueId == KashimoTechniqueSelectionService.NYOI_STAFF_RECALL_SELECT) {
            return new TechniqueSelectionCandidate(
                selectTechniqueId,
                KashimoTechniqueSelectionService.NYOI_STAFF_RECALL_NAME_KEY,
                true,
                true,
                0.0D
            );
        }

        if (curseTechniqueId == KugisakiTechniqueSelectionService.CURSE_TECHNIQUE_ID
            && selectTechniqueId == KugisakiTechniqueSelectionService.NAIL_REFILL_SELECT) {
            return new TechniqueSelectionCandidate(
                selectTechniqueId,
                KugisakiTechniqueSelectionService.NAIL_REFILL_NAME_KEY,
                true,
                true,
                0.0D
            );
        }

        if (MegumiShadowTechniqueSelectionService.isShadowSelection(curseTechniqueId, selectTechniqueId)) {
            return new TechniqueSelectionCandidate(
                selectTechniqueId,
                MegumiShadowTechniqueSelectionService.SHADOW_NAME_KEY,
                true,
                false,
                0.0D
            );
        }

        return TechniqueSelectionCandidate.none(selectTechniqueId);
    }
}
