package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueDisplayNameResolver;
import net.minecraft.world.entity.Entity;

public final class OTechniqueNameProcedureHook {
    private OTechniqueNameProcedureHook() {
    }

    public static String resolveDisplayName(Entity entity, String key) {
        if (entity == null || key == null || key.isEmpty()) {
            return JjaTechniqueDisplayNameResolver.resolveDisplayName(entity, 0, 0, key);
        }
        var playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(entity);
        int curseTechniqueId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(playerVars);
        int selectTechniqueId = (int) Math.round(playerVars.PlayerSelectCurseTechnique);
        return JjaTechniqueDisplayNameResolver.resolveDisplayName(entity, curseTechniqueId, selectTechniqueId, key);
    }
}
