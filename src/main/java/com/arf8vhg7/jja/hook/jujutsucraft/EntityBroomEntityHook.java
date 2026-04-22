package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import net.mcreator.jujutsucraft.entity.NishimiyaMomoEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class EntityBroomEntityHook {
    private static final int NISHIMIYA_TECHNIQUE_ID = 36;

    private EntityBroomEntityHook() {
    }

    public static void applyJjaNoGravity(Entity entity) {
        if (entity == null) {
            return;
        }

        boolean noGravity = shouldKeepNoGravity(entity);

        try {
            entity.getClass().getMethod("m_20242_", boolean.class).invoke(entity, noGravity);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to apply broom gravity state", exception);
        }
    }

    private static boolean shouldKeepNoGravity(Entity entity) {
        Entity owner = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(entity.level(), entity);
        if (owner instanceof NishimiyaMomoEntity) {
            return true;
        }
        return owner instanceof Player player && JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player) == NISHIMIYA_TECHNIQUE_ID;
    }
}