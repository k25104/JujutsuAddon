package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuRikaRules;
import net.minecraft.world.item.ItemStack;

public final class SwordOkkotsuYutaToolInInventoryTickProcedureHook {
    private SwordOkkotsuYutaToolInInventoryTickProcedureHook() {
    }

    public static boolean shouldShrinkCopiedTechnique(ItemStack itemStack) {
        return !OkkotsuRikaRules.consumeCopiedTechniquePreserveMarker(itemStack);
    }

    public static void clearStaleCopiedTechniquePreserveMarker(ItemStack itemStack) {
        OkkotsuRikaRules.clearStaleCopiedTechniquePreserveMarker(itemStack);
    }
}
