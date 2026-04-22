package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuCopiedTechniqueRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class TechniqueImitation1ProcedureHook {
    private static final String GIVE_LOUDSPEAKER_COMMAND = "give @s jujutsucraft:loudspeaker";

    private TechniqueImitation1ProcedureHook() {
    }

    public static boolean shouldReplaceMegaphoneGrant(Entity entity, String command) {
        return entity instanceof Player && GIVE_LOUDSPEAKER_COMMAND.equals(command);
    }

    public static void grantMegaphone(Player player) {
        OkkotsuCopiedTechniqueRules.grantMegaphone(player);
    }
}
