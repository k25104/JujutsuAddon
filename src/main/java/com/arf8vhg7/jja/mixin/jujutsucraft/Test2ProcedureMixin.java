package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.Test2ProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.mcreator.jujutsucraft.procedures.Test2Procedure;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Test2Procedure.class, remap = false)
public abstract class Test2ProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$compareGetoSelectionName(
        String currentName,
        Object expectedName,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (Test2ProcedureHook.isGetoSlotSelectionActive(entity)) {
            return Test2ProcedureHook.compareGetoSelectionName(currentName, expectedName);
        }
        return original.call(currentName, expectedName);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerSelectCurseTechniqueName:Ljava/lang/String;",
            opcode = Opcodes.GETFIELD
        ),
        remap = false
    ,
        require = 1
    )
    private static String jja$resolveSelectTechniqueName(String original, @Local(argsOnly = true) Entity entity) {
        return Test2ProcedureHook.resolveSelectTechniqueName(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$applyGetoSummonEnhancementAfterOwnershipRestore(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity,
        @Local Entity entityiterator
    ) {
        original.call(tag, key, value);
        if ("friend_num_worker".equals(key)) {
            Test2ProcedureHook.onGetoSummonReleased(entity, entityiterator);
        }
    }
}
