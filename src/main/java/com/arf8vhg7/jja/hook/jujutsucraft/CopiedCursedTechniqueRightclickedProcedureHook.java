package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuRikaRules;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.JjaTechniqueUseDenialHelper;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueHeldItemRules;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueSkillResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CopiedCursedTechniqueRightclickedProcedureHook {
    private CopiedCursedTechniqueRightclickedProcedureHook() {
    }

    public static boolean shouldCancelUse(Entity entity, ItemStack itemStack) {
        if (!(entity instanceof Player player) || itemStack == null || itemStack.isEmpty()) {
            return false;
        }
        int skillId = TechniqueSkillResolver.resolveCopiedSkillId(itemStack);
        if (skillId <= 0 || itemStack.getOrCreateTag().getBoolean("Used")) {
            return false;
        }
        if (TechniqueHeldItemRules.canUseSkill(player, skillId)) {
            return false;
        }
        JjaTechniqueUseDenialHelper.deny(player, true, true);
        return true;
    }

    public static void markCopiedTechniqueUseForPreservation(Entity entity, ItemStack itemStack) {
        OkkotsuRikaRules.markCopiedTechniqueUseForPreservation(entity, itemStack);
    }
}
