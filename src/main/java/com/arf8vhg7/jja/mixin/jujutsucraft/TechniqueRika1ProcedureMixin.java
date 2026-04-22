package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueRika1ProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.TechniqueRika1Procedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueRika1Procedure.class, remap = false)
public abstract class TechniqueRika1ProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;m_7967_(Lnet/minecraft/world/entity/Entity;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$queueCeRefillOnRika2Summon(
        ServerLevel serverLevel,
        Entity summoned,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        boolean added = original.call(serverLevel, summoned);
        if (added) {
            TechniqueRika1ProcedureHook.onRikaSummoned(entity, summoned);
        }
        return added;
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;m_7967_(Lnet/minecraft/world/entity/Entity;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$queueCeRefillOnRikaSummon(
        ServerLevel serverLevel,
        Entity summoned,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        boolean added = original.call(serverLevel, summoned);
        if (added) {
            TechniqueRika1ProcedureHook.onRikaSummoned(entity, summoned);
        }
        return added;
    }
}
