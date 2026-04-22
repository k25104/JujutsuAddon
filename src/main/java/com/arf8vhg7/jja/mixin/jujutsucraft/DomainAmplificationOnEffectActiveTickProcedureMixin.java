package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainAmplificationOnEffectActiveTickProcedureHook;
import net.mcreator.jujutsucraft.procedures.DomainAmplificationOnEffectActiveTickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DomainAmplificationOnEffectActiveTickProcedure.class, remap = false)
public abstract class DomainAmplificationOnEffectActiveTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$awardNearbyWitnesses(Entity entity, CallbackInfo ci) {
        DomainAmplificationOnEffectActiveTickProcedureHook.onActiveTick(entity);
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 5), remap = false, require = 1)
    private static int jja$modifyTickInterval(int original) {
        return DomainAmplificationOnEffectActiveTickProcedureHook.modifyTickInterval(original);
    }

    @ModifyConstant(method = "lambda$execute$0", constant = @Constant(doubleValue = 10.0), remap = false, require = 1)
    private static double jja$modifyCursePowerDrain(double original) {
        return DomainAmplificationOnEffectActiveTickProcedureHook.modifyCursePowerDrain(original);
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
    private static boolean jja$skipUnstableAdd(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return DomainAmplificationOnEffectActiveTickProcedureHook.addEffect(livingEntity, effectInstance);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_6844_(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false,
        require = 1
    )
    private static ItemStack jja$resolveCuriosEquipmentRead(
        LivingEntity livingEntity,
        EquipmentSlot equipmentSlot,
        Operation<ItemStack> original
    ) {
        return DomainAmplificationOnEffectActiveTickProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }
}
