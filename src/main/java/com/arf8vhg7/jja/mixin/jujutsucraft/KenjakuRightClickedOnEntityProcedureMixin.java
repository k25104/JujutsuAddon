package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KenjakuRightClickedOnEntityProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.KenjakuRightClickedOnEntityProcedure;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KenjakuRightClickedOnEntityProcedure.class, remap = false)
public abstract class KenjakuRightClickedOnEntityProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerAdvancements;m_135988_(Lnet/minecraft/advancements/Advancement;Ljava/lang/String;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$suppressLegacyDomainAmplificationGrant(
        PlayerAdvancements playerAdvancements,
        Advancement advancement,
        String criterion,
        Operation<Boolean> original
    ) {
        if (KenjakuRightClickedOnEntityProcedureHook.shouldSuppressLegacyDomainAmplificationGrant()) {
            return false;
        }
        return original.call(playerAdvancements, advancement, criterion);
    }
}
