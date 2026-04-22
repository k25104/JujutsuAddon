package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.EntityBroomEntityHook;
import net.mcreator.jujutsucraft.entity.EntityBroomEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityBroomEntity.class, remap = false)
public abstract class EntityBroomEntityMixin {
    @Inject(method = "m_8107_", at = @At("HEAD"), remap = false, require = 1)
    private void jja$applyNoGravityOnTick(CallbackInfo ci) {
        EntityBroomEntityHook.applyJjaNoGravity((Entity) (Object) this);
    }
}