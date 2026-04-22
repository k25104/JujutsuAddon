package com.arf8vhg7.jja.compat.jujutsucraft;

import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class JjaJujutsucraftCompat {
    private JjaJujutsucraftCompat() {
    }

    @Nullable
    public static JujutsucraftModVariables.PlayerVariables jjaGetPlayerVariables(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).resolve().orElse(null);
    }

    public static JujutsucraftModVariables.PlayerVariables jjaResolvePlayerVariablesOrDefault(
        @Nullable JujutsucraftModVariables.PlayerVariables variables
    ) {
        return variables != null ? variables : new JujutsucraftModVariables.PlayerVariables();
    }

    public static JujutsucraftModVariables.PlayerVariables jjaGetPlayerVariablesOrDefault(@Nullable Entity entity) {
        return jjaResolvePlayerVariablesOrDefault(jjaGetPlayerVariables(entity));
    }

    public static double jjaGetPlayerFame(@Nullable Entity entity) {
        JujutsucraftModVariables.PlayerVariables variables = jjaGetPlayerVariables(entity);
        if (variables == null) {
            return 0.0;
        }
        return variables.PlayerFame;
    }

    public static double jjaGetPlayerTechniqueUsedNumber(@Nullable Entity entity) {
        JujutsucraftModVariables.PlayerVariables variables = jjaGetPlayerVariables(entity);
        if (variables == null) {
            return 0.0;
        }
        return variables.PlayerTechniqueUsedNumber;
    }

    public static int jjaGetActiveCurseTechniqueId(@Nullable Entity entity) {
        return jjaGetActiveCurseTechniqueId(jjaGetPlayerVariables(entity));
    }

    public static int jjaGetActiveCurseTechniqueId(@Nullable JujutsucraftModVariables.PlayerVariables variables) {
        if (variables == null) {
            return 0;
        }
        double activeTechnique = variables.SecondTechnique ? variables.PlayerCurseTechnique2 : variables.PlayerCurseTechnique;
        return (int) Math.round(activeTechnique);
    }

    public static boolean jjaHasNeutralization(@Nullable LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        return entity.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.NEUTRALIZATION.get()));
    }
}
