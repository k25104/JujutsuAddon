package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public final class SkillWiFiProcedureHook {
    private SkillWiFiProcedureHook() {
    }

    public static MutableComponent buildTakabaMessage() {
        return Component.translatable("jujutsu.message.takaba1").withStyle(net.minecraft.ChatFormatting.BOLD);
    }

    public static void applySelfHeal(LivingEntity livingEntity, float health, Operation<Void> original) {
        if (!FirstAidMutationService.applyDistributedMaxFractionHeal(livingEntity, 0.5D)) {
            original.call(livingEntity, health);
        }
    }
}
