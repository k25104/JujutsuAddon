package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CopiedCursedTechniqueRightclickedProcedureHook;
import net.mcreator.jujutsucraft.procedures.CopiedCursedTechniqueRightclickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CopiedCursedTechniqueRightclickedProcedure.class, remap = false)
public abstract class CopiedCursedTechniqueRightclickedProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$blockRestrictedCopiedTechniques(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        ItemStack itemstack,
        CallbackInfo ci
    ) {
        if (CopiedCursedTechniqueRightclickedProcedureHook.shouldCancelUse(entity, itemstack)) {
            ci.cancel();
        }
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$markCopiedTechniqueUseForPreservation(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        ItemStack itemstack,
        CallbackInfo ci
    ) {
        CopiedCursedTechniqueRightclickedProcedureHook.markCopiedTechniqueUseForPreservation(entity, itemstack);
    }
}
