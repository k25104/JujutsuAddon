package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WombProfusionActiveProcedureHook;
import net.mcreator.jujutsucraft.procedures.WombProfusionActiveProcedure;
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

@Mixin(value = WombProfusionActiveProcedure.class, remap = false)
public abstract class WombProfusionActiveProcedureMixin {
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
        return WombProfusionActiveProcedureHook.getEffect(livingEntity, effect);
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
        return WombProfusionActiveProcedureHook.adjustDomainRange(entity instanceof LivingEntity livingEntity ? livingEntity : null, radius);
    }

}
