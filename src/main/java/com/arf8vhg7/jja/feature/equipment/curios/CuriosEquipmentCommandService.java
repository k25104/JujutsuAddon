package com.arf8vhg7.jja.feature.equipment.curios;

import net.minecraft.world.entity.Entity;

public final class CuriosEquipmentCommandService {
    private CuriosEquipmentCommandService() {
    }

    public static boolean willHandleEquipCommand(Entity entity, String command) {
        return CuriosEquipmentMutationService.willHandleEquipCommand(entity, command);
    }

    public static boolean tryHandleEquipCommand(Entity entity, String command) {
        return CuriosEquipmentMutationService.tryHandleEquipCommand(entity, command);
    }

    public static void handlePostCommandCleanup(Entity entity, String command) {
        CuriosEquipmentMutationService.handlePostCommandCleanup(entity, command);
    }
}
