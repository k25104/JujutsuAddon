package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvReturnPositionService;
import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvTrailBlockService;
import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvTrailPlacementContext;
import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuCopyDhruvCleanupBindingResolver;
import net.minecraft.core.BlockPos;
import java.util.function.DoubleSupplier;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public final class AIShikigamiDhruvLakdawallaProcedureHook {
    private static final String DHRUV_TRAIL_SETBLOCK_COMMAND = "setblock ~ ~ ~ jujutsucraft:domain keep";

    private AIShikigamiDhruvLakdawallaProcedureHook() {
    }

    public static double resolvePlacementDistance(Entity entity, DoubleSupplier fallback) {
        return DhruvTrailBlockService.isTrailPlacementEntity(entity) ? 0.0D : fallback.getAsDouble();
    }

    public static double resolveIdlePlacementRoll(Entity entity, DoubleSupplier fallback) {
        return DhruvTrailBlockService.isTrailPlacementEntity(entity) ? 0.0D : fallback.getAsDouble();
    }

    public static void enforceOwnerRange(LevelAccessor world, Entity entity) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        DhruvReturnPositionService.enforceOwnerDistance(serverLevel, entity);
    }

    public static int preserveTrailLifetime(
        Commands commands,
        CommandSourceStack commandSourceStack,
        String command,
        Entity entity,
        Operation<Integer> original
    ) {
        if (!DhruvTrailBlockService.isTrailPlacementEntity(entity) || !DHRUV_TRAIL_SETBLOCK_COMMAND.equals(command)) {
            return original.call(commands, commandSourceStack, command);
        }
        int result = DhruvTrailPlacementContext.withPlacement(entity.getUUID(), () -> original.call(commands, commandSourceStack, command));
        ServerLevel serverLevel = commandSourceStack.getLevel();
        Vec3 position = commandSourceStack.getPosition();
        UUID cleanupBindingUuid = OkkotsuCopyDhruvCleanupBindingResolver.resolveCleanupBindingUuid(serverLevel, entity);
        DhruvTrailBlockService.registerTrailBlock(
            serverLevel,
            BlockPos.containing(position.x, position.y, position.z),
            entity,
            cleanupBindingUuid
        );
        return result;
    }
}
