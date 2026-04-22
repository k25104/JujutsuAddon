package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.summon.ShikigamiReleaseKillSuppressionContext;
import net.minecraft.world.entity.Entity;

public final class ShikigamiRightClickedOnEntityProcedureHook {
    private static final String SELF_KILL_COMMAND = "kill @s";

    private ShikigamiRightClickedOnEntityProcedureHook() {
    }

    public static boolean shouldSuppressSelfKill(Entity entity, String command) {
        return entity != null
            && SELF_KILL_COMMAND.equals(command)
            && ShikigamiReleaseKillSuppressionContext.isActive();
    }
}
