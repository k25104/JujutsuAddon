package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.hook.jujutsucraft.KeyChangeTechniqueOnKeyPressedProcedureHook;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyChangeTechniqueOnKeyPressedProcedure.class, remap = false)
public abstract class KeyChangeTechniqueOnKeyPressedProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$handleCustomSelection(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (KeyChangeTechniqueOnKeyPressedProcedureHook.handleCustomSelection(world, x, y, z, entity)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z",
            ordinal = 0,
            shift = At.Shift.BEFORE
        ),
        remap = false,
        require = 1
    )
    private static void jja$injectGojoTeleportSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci,
        @Local(index = 8) LocalRef<String> name,
        @Local(index = 9) LocalBooleanRef passive,
        @Local(index = 10) LocalBooleanRef physical,
        @Local(index = 11) LocalDoubleRef cost,
        @Local(index = 13) double playerCt,
        @Local(index = 15) double playerSelect
    ) {
        TechniqueSelectionCandidate candidate = KeyChangeTechniqueOnKeyPressedProcedureHook.resolvePageOneSupplement(
            entity,
            playerCt,
            playerSelect,
            name.get()
        );
        if (candidate.isEmpty()) {
            return;
        }
        name.set(candidate.name());
        passive.set(candidate.passive());
        physical.set(candidate.physical());
        cost.set(candidate.cost());
    }
}
