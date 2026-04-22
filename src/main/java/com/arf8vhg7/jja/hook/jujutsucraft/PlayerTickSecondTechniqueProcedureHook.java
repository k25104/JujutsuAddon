package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuCopiedTechniqueRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class PlayerTickSecondTechniqueProcedureHook {
    private PlayerTickSecondTechniqueProcedureHook() {
    }

    public static ItemStack resolveHeldMegaphoneAwareStack(Entity entity, ItemStack mainHandItem) {
        return OkkotsuCopiedTechniqueRules.resolveHeldTechniqueStack(entity, mainHandItem);
    }
}
