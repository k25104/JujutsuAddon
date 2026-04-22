package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueNameKeyResolver;
import com.arf8vhg7.jja.feature.jja.technique.family.kugisaki.KugisakiTechniqueSelectionService;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class KeyChangeTechniqueSelectionPageProceduresHook {
    private KeyChangeTechniqueSelectionPageProceduresHook() {
    }

    public static String jjaGetKeyOrString(Component component) {
        return JjaTechniqueNameKeyResolver.jjaGetKeyOrString(component);
    }

    public static boolean handleCustomPage4Selection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect
    ) {
        return KugisakiTechniqueSelectionService.tryHandlePageSelection(world, x, y, z, entity, playerCt, playerSelect);
    }
}
