package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import java.util.function.Supplier;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class OgiZeninPassiveSkillProcedureHook {
    private OgiZeninPassiveSkillProcedureHook() {
    }

    public static JujutsucraftModVariables.PlayerVariables resolvePlayerVariablesOrDefault(
        JujutsucraftModVariables.PlayerVariables variables
    ) {
        return resolveOrDefault(variables, () -> JjaJujutsucraftCompat.jjaResolvePlayerVariablesOrDefault(null));
    }

    static <T> T resolveOrDefault(T value, Supplier<T> fallbackSupplier) {
        return value != null ? value : fallbackSupplier.get();
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }
}
