package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueNameKeyResolver;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public final class SummonNueProcedureHook {
    private SummonNueProcedureHook() {
    }

    public static String jjaGetKeyOrString(Component component) {
        return JjaTechniqueNameKeyResolver.jjaGetKeyOrString(component);
    }

    public static String resolveSelectTechniqueName(Entity entity, String currentName) {
        return RegisteredCurseTechniqueSlots.resolveRegisteredTechniqueName(entity, currentName);
    }
}
