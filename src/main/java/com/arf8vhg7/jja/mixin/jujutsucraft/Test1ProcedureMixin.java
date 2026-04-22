package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.Test1ProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.mcreator.jujutsucraft.procedures.Test1Procedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Test1Procedure.class, remap = false)
public abstract class Test1ProcedureMixin {
    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128359_(Ljava/lang/String;Ljava/lang/String;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$clearRecoveredGetoSummonEnhancement(
        CompoundTag tag,
        String key,
        String value,
        Operation<Void> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Local(argsOnly = true, ordinal = 1) Entity sourceentity
    ) {
        if ("owner_name_data".equals(key)) {
            Test1ProcedureHook.onGetoCursedSpiritRecovered(sourceentity, entity);
        }
        original.call(tag, key, value);
    }
}
