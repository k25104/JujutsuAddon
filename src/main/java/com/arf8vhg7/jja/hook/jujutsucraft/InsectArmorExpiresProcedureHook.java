package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentCommandService;
import java.util.function.IntSupplier;
import net.minecraft.world.entity.Entity;

public final class InsectArmorExpiresProcedureHook {
    private InsectArmorExpiresProcedureHook() {
    }

    public static int runArmorCommand(String command, Entity entity, IntSupplier fallback) {
        if (CuriosEquipmentCommandService.tryHandleEquipCommand(entity, command)) {
            return 1;
        }
        int result = fallback.getAsInt();
        CuriosEquipmentCommandService.handlePostCommandCleanup(entity, command);
        return result;
    }
}
