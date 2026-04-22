package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.IdleDeathGambleActiveProcedureHook;
import net.mcreator.jujutsucraft.procedures.IdleDeathGambleActiveProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.objectweb.asm.Opcodes;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(value = IdleDeathGambleActiveProcedure.class, remap = false)
public abstract class IdleDeathGambleActiveProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"
        ),
        remap = false
    ,
        require = 1
    )
    private static MobEffectInstance jja$replaceDomainDuration(LivingEntity livingEntity, MobEffect effect, Operation<MobEffectInstance> original) {
        return IdleDeathGambleActiveProcedureHook.getEffect(livingEntity, effect);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$normalizeDomainDuration(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return IdleDeathGambleActiveProcedureHook.addEffect(livingEntity, effectInstance);
    }
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$adjustDomainRange(
        double radius,
        LevelAccessor world,
        Entity entity
    ) {
        return IdleDeathGambleActiveProcedureHook.adjustDomainRange(entity instanceof LivingEntity livingEntity ? livingEntity : null, radius);
    }

}
