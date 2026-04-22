package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.MalevolentShrineActiveProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.mcreator.jujutsucraft.procedures.MalevolentShrineActiveProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = MalevolentShrineActiveProcedure.class, remap = false)
public abstract class MalevolentShrineActiveProcedureMixin {
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
        return MalevolentShrineActiveProcedureHook.getEffect(livingEntity, effect);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false,
        require = 1
    )
    private static double jja$adjustDomainRange(
        double radius,
        LevelAccessor world,
        Entity entity
    ) {
        return MalevolentShrineActiveProcedureHook.adjustDomainRange(entity instanceof LivingEntity livingEntity ? livingEntity : null, radius);
    }

    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;round(D)J",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        remap = false
    ,
        require = 1
    )
    private static void jja$extraTerrainDestruction(
        LevelAccessor world,
        Entity entity,
        CallbackInfo ci,
        String STR1,
        double old_skill,
        double count_A,
        double z_center,
        double range,
        double old_cooldown,
        double x_pos,
        double z_pos,
        double y_center,
        double dis,
        double dust_amount,
        double x_center
    ) {
        MalevolentShrineActiveProcedureHook.tryExtraTerrainDestruction(world, entity, range, x_center, y_center, z_center);
    }
}
