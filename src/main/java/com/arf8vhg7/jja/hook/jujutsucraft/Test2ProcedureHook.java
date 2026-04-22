package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementService;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import net.minecraft.world.entity.Entity;

public final class Test2ProcedureHook {
    private Test2ProcedureHook() {
    }

    public static boolean isGetoSlotSelectionActive(Entity entity) {
        return RegisteredCurseTechniqueSlots.resolveGetoRegisteredTechniqueName(entity) != null;
    }

    public static String resolveSelectTechniqueName(Entity entity, String currentName) {
        String getoRegisteredName = RegisteredCurseTechniqueSlots.resolveGetoRegisteredTechniqueName(entity);
        return getoRegisteredName != null ? getoRegisteredName : currentName;
    }

    public static boolean compareGetoSelectionName(String currentName, Object expectedName) {
        String expectedNameString = expectedName instanceof String ? (String) expectedName : null;
        String normalizedCurrentName = RegisteredCurseTechniqueSlots.normalizeGetoTechniqueName(currentName);
        String normalizedExpectedName = RegisteredCurseTechniqueSlots.normalizeGetoTechniqueName(expectedNameString);
        return normalizedCurrentName == null ? normalizedExpectedName == null : normalizedCurrentName.equals(normalizedExpectedName);
    }

    public static void onGetoSummonReleased(Entity owner, Entity summon) {
        SummonEnhancementService.tryApplyPending(owner, summon);
    }
}
