package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentMutationService;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.mcreator.jujutsucraft.JujutsucraftMod;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class MythicalBeastAmberEffectEffectExpiresProcedureHook {
    private MythicalBeastAmberEffectEffectExpiresProcedureHook() {
    }

    public static void markForceDeathBypass(Entity entity) {
        ReviveFlowService.markForceDeathBypass(entity);
    }

    public static void scheduleManagedCleanup(Entity entity) {
        JujutsucraftMod.queueServerWork(
            1,
            () -> {
                if (!(entity instanceof LivingEntity livingEntity)
                    || livingEntity.hasEffect(JujutsucraftModMobEffects.MYTHICAL_BEAST_AMBER_EFFECT.get())) {
                    return;
                }
                CuriosEquipmentMutationService.clearManagedItem(
                    entity,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("jujutsucraft", "mythical_beast_amber_helmet")
                );
            }
        );
    }
}
