package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.Demotion1RightclickedProcedureHook;
import net.mcreator.jujutsucraft.procedures.Demotion1RightclickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Demotion1RightclickedProcedure.class, remap = false)
public abstract class Demotion1RightclickedProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$handleSpecialDemotion(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack, CallbackInfo ci) {
        if (Demotion1RightclickedProcedureHook.handle(world, x, y, z, entity, itemStack)) {
            ci.cancel();
        }
    }

    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$beginFirstAidReset(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack, CallbackInfo ci) {
        Demotion1RightclickedProcedureHook.beginFirstAidReset(entity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$finishFirstAidReset(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack, CallbackInfo ci) {
        Demotion1RightclickedProcedureHook.finishFirstAidReset(entity);
    }
}
