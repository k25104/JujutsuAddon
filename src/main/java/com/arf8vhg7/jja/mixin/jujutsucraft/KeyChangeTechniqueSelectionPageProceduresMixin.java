package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ChangeTechniqueTestProcedureHook;
import com.arf8vhg7.jja.hook.jujutsucraft.KeyChangeTechniqueSelectionPageProceduresHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed3Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed4Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed5Procedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {
        KeyChangeTechniqueOnKeyPressed3Procedure.class,
        KeyChangeTechniqueOnKeyPressed4Procedure.class,
        KeyChangeTechniqueOnKeyPressed5Procedure.class
    },
    remap = false
)
public abstract class KeyChangeTechniqueSelectionPageProceduresMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;DD)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$handleCustomPage4Selection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect,
        CallbackInfo ci
    ) {
        if (KeyChangeTechniqueSelectionPageProceduresHook.handleCustomPage4Selection(world, x, y, z, entity, playerCt, playerSelect)) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"),
        remap = false,
        require = 1
    )
    private static String jja$getKeyOrString(MutableComponent component, Operation<String> original) {
        return KeyChangeTechniqueSelectionPageProceduresHook.jjaGetKeyOrString(component);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/ChangeTechniqueTestProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;DD)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$evaluateCandidate(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect,
        Operation<Boolean> original,
        @Local(ordinal = 0) String name
    ) {
        return ChangeTechniqueTestProcedureHook.evaluateCandidate(
            world,
            x,
            y,
            z,
            entity,
            playerCt,
            playerSelect,
            name,
            () -> original.call(world, x, y, z, entity, playerCt, playerSelect)
        );
    }
}
