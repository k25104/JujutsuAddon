package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.GetDomainBlockProcedureHook;
import net.mcreator.jujutsucraft.procedures.GetDomainBlockProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GetDomainBlockProcedure.class, remap = false)
public abstract class GetDomainBlockProcedureMixin {
    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$replaceChimeraShadowGardenFloor(Entity entity, CallbackInfo callbackInfo) {
        GetDomainBlockProcedureHook.replaceChimeraShadowGardenFloor(entity);
    }
}
