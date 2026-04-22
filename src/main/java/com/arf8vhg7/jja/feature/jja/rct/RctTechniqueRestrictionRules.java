package com.arf8vhg7.jja.feature.jja.rct;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public final class RctTechniqueRestrictionRules {
    private static final Set<ResourceLocation> CURSED_SPIRIT_LOCKED_ADVANCEMENTS = Set.of(
        RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID,
        RctAdvancementHelper.MASTERY_RCT_BRAIN_DESTRUCTION_ID,
        RctAdvancementHelper.MASTERY_RCT_BRAIN_REGENERATION_ID
    );

    private RctTechniqueRestrictionRules() {
    }

    public static boolean canUnlockAdvancement(boolean cursedSpirit, ResourceLocation advancementId) {
        return !cursedSpirit || advancementId == null || !CURSED_SPIRIT_LOCKED_ADVANCEMENTS.contains(advancementId);
    }

    public static boolean canUseOutput(boolean cursedSpirit, boolean unlocked, boolean enabled) {
        return canUseOutput(cursedSpirit, unlocked, enabled, false);
    }

    public static boolean canUseOutput(boolean cursedSpirit, boolean unlocked, boolean enabled, boolean runtimeChannelActive) {
        return !cursedSpirit && unlocked && (enabled || runtimeChannelActive);
    }

    public static boolean canApplyOutput(boolean cursedSpirit, boolean unlocked, boolean enabled, boolean runtimeChannelActive) {
        return !cursedSpirit && unlocked && enabled && runtimeChannelActive;
    }

    public static boolean canUseBrainRegeneration(boolean cursedSpirit, boolean hasBrainDamage, boolean unlocked, boolean enabled) {
        return !cursedSpirit && hasBrainDamage && unlocked && enabled;
    }

    public static boolean canStartBrainDestruction(boolean cursedSpirit, boolean unlocked, boolean unstable) {
        return !cursedSpirit && unlocked && unstable;
    }
}
