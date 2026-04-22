package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SetTagCursedSpritProcedureHook;
import net.mcreator.jujutsucraft.procedures.SetTagCursedSpritProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SetTagCursedSpritProcedure.class, remap = false)
public abstract class SetTagCursedSpritProcedureMixin {
    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$clearRika2CursedSpiritTag(LevelAccessor world, Entity entity, CallbackInfo ci) {
        SetTagCursedSpritProcedureHook.clearRika2CursedSpiritTag(entity);
    }
}
