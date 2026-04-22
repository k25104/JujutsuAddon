package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.BlueEntityHook;
import net.mcreator.jujutsucraft.entity.BlueEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlueEntity.class, remap = false)
public abstract class BlueEntityMixin {
    public boolean m_6094_() {
        return BlueEntityHook.isPushable();
    }

    protected void m_7324_(Entity entity) {
        if (BlueEntityHook.shouldCancelEntityPush(entity)) {
            return;
        }
    }

    protected void m_6138_() {
        if (BlueEntityHook.shouldCancelPushEntities()) {
            return;
        }
    }
}
