package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.MasterSkillsProcedureHook;
import net.mcreator.jujutsucraft.procedures.MasterSkillsProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MasterSkillsProcedure.class, remap = false)
public abstract class MasterSkillsProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$handleRctMasteryItem(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack, CallbackInfo ci) {
        if (MasterSkillsProcedureHook.handle(world, x, y, z, entity, itemStack)) {
            ci.cancel();
        }
    }
}
