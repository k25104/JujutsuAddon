package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.gojo.GojoTechniqueSelectionService;
import com.arf8vhg7.jja.feature.jja.technique.family.kashimo.KashimoTechniqueSelectionService;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowTechniqueSelectionService;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaAddonTechniqueSelectionCatalog;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class KeyChangeTechniqueOnKeyPressedProcedureHook {
    private KeyChangeTechniqueOnKeyPressedProcedureHook() {
    }

    public static boolean handleCustomSelection(LevelAccessor world, double x, double y, double z, Entity entity) {
        return GojoTechniqueSelectionService.tryHandlePageSelection(world, x, y, z, entity)
            || KashimoTechniqueSelectionService.tryHandlePageSelection(world, x, y, z, entity)
            || MegumiShadowTechniqueSelectionService.tryHandlePageSelection(world, x, y, z, entity);
    }

    public static TechniqueSelectionCandidate resolvePageOneSupplement(Entity entity, double playerCt, double playerSelect, String currentName) {
        TechniqueSelectionCandidate addonCandidate = JjaAddonTechniqueSelectionCatalog.resolveCandidate(
            (int) Math.round(playerCt),
            (int) Math.round(playerSelect)
        );
        if (!addonCandidate.isEmpty()) {
            return addonCandidate;
        }
        TechniqueSelectionCandidate gojoCandidate = GojoTechniqueSelectionService.resolvePageOneSupplement(entity, playerCt, playerSelect, currentName);
        if (!gojoCandidate.isEmpty()) {
            return gojoCandidate;
        }
        return TechniqueSelectionCandidate.none(playerSelect);
    }
}
