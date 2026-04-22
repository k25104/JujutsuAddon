package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueService;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;

public final class CounterProcedureHook {
    private static final double KUSAKABE_CURSE_TECHNIQUE_ID = 31.0D;

    private CounterProcedureHook() {
    }

    public static boolean shouldSkipCounter(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        return shouldSkipCounter(
            true,
            hasKusakabeTechnique(playerVariables),
            AntiDomainTechniqueService.hasOwnedSimpleDomain(entity),
            AntiDomainTechniqueService.shouldSuppressSimpleDomainDerivedEffects(entity)
        );
    }

    public static boolean shouldRemoveCounterEffect(Entity entity, MobEffect effect) {
        if (effect != JujutsucraftModMobEffects.SIMPLE_DOMAIN.get() && effect != JujutsucraftModMobEffects.FALLING_BLOSSOM_EMOTION.get()) {
            return true;
        }
        return !AntiDomainTechniqueService.shouldPreserveCounterAntiDomainEffects(entity);
    }

    static boolean shouldSkipCounter(
        boolean playerControlled,
        boolean hasKusakabeTechnique,
        boolean hasOwnedSimpleDomain,
        boolean suppressSimpleDomainDerivedEffects
    ) {
        return playerControlled && hasKusakabeTechnique && hasOwnedSimpleDomain && suppressSimpleDomainDerivedEffects;
    }

    static boolean hasKusakabeTechnique(JujutsucraftModVariables.PlayerVariables playerVariables) {
        return hasKusakabeTechnique(
            playerVariables != null ? playerVariables.PlayerCurseTechnique : 0.0D,
            playerVariables != null ? playerVariables.PlayerCurseTechnique2 : 0.0D
        );
    }

    static boolean hasKusakabeTechnique(double playerCurseTechnique, double playerCurseTechnique2) {
        return playerCurseTechnique == KUSAKABE_CURSE_TECHNIQUE_ID || playerCurseTechnique2 == KUSAKABE_CURSE_TECHNIQUE_ID;
    }
}
