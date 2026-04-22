package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class TechniqueSkillResolver {
    private TechniqueSkillResolver() {
    }

    public static int resolveSelectedSkillId(Entity entity) {
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (playerVars == null) {
            return 0;
        }
        int techniqueId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(playerVars);
        int select = (int) Math.round(playerVars.PlayerSelectCurseTechnique);
        if (techniqueId == 0) {
            return 0;
        }
        return techniqueId * 100 + select;
    }

    public static int resolveCurrentSkillId(Entity entity) {
        return JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity);
    }

    public static int resolveCopiedSkillId(ItemStack stack) {
        return JjaJujutsucraftDataAccess.jjaGetCopiedSkillId(stack);
    }

    public static boolean hasTechnique(Player player, int techniqueId) {
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVars == null) {
            return false;
        }
        return (int) Math.round(playerVars.PlayerCurseTechnique) == techniqueId
            || (int) Math.round(playerVars.PlayerCurseTechnique2) == techniqueId;
    }
}
