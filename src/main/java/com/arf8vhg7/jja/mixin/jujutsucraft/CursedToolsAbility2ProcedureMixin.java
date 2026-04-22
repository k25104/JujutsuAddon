package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CursedToolsAbility2ProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.CursedToolsAbility2Procedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CursedToolsAbility2Procedure.class, remap = false)
public abstract class CursedToolsAbility2ProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V",
            ordinal = 0
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$clampPowerDamageBonusPositive(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(tag, key, CursedToolsAbility2ProcedureHook.resolveHeldItemDamageBonus(entity, tag, key, value));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$clampPowerDamageBonusNegativePath(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(tag, key, CursedToolsAbility2ProcedureHook.resolveHeldItemDamageBonus(entity, tag, key, value));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V",
            ordinal = 4
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$clampPlayfulCloudDamageBonus(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(tag, key, CursedToolsAbility2ProcedureHook.resolveHeldItemDamageBonus(entity, tag, key, value));
    }

    @Definition(id = "getOrCreateTag", method = "Lnet/minecraft/world/item/ItemStack;m_41784_()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getOrCreateTag().getDouble('Power')")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0), require = 1)
    private static double jja$zeroExtraArmHeldItemPowerFirstRead(double original, @Local(argsOnly = true) Entity entity) {
        return CursedToolsAbility2ProcedureHook.resolveHeldItemPowerBonus(entity, original);
    }

    @Definition(id = "getOrCreateTag", method = "Lnet/minecraft/world/item/ItemStack;m_41784_()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getOrCreateTag().getDouble('Power')")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1), require = 1)
    private static double jja$zeroExtraArmHeldItemPowerSecondRead(double original, @Local(argsOnly = true) Entity entity) {
        return CursedToolsAbility2ProcedureHook.resolveHeldItemPowerBonus(entity, original);
    }

    @Definition(id = "getOrCreateTag", method = "Lnet/minecraft/world/item/ItemStack;m_41784_()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getOrCreateTag().getDouble('Power')")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 2), require = 1)
    private static double jja$zeroExtraArmHeldItemPowerThirdRead(double original, @Local(argsOnly = true) Entity entity) {
        return CursedToolsAbility2ProcedureHook.resolveHeldItemPowerBonus(entity, original);
    }

    @Definition(id = "getOrCreateTag", method = "Lnet/minecraft/world/item/ItemStack;m_41784_()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getOrCreateTag().getDouble('Power')")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 3), require = 1)
    private static double jja$zeroExtraArmHeldItemPowerFourthRead(double original, @Local(argsOnly = true) Entity entity) {
        return CursedToolsAbility2ProcedureHook.resolveHeldItemPowerBonus(entity, original);
    }
}
