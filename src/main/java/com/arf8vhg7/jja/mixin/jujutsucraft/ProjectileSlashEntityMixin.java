package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ProjectileSlashEntityHook;
import net.mcreator.jujutsucraft.entity.ProjectileSlashEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ProjectileSlashEntity.class, remap = false)
public abstract class ProjectileSlashEntityMixin {
    public boolean m_6094_() {
        return ProjectileSlashEntityHook.isPushable();
    }

    protected void m_7324_(Entity entity) {
        if (ProjectileSlashEntityHook.shouldCancelEntityPush(entity)) {
            return;
        }
    }

    protected void m_6138_() {
        if (ProjectileSlashEntityHook.shouldCancelPushEntities()) {
            return;
        }
    }
}
