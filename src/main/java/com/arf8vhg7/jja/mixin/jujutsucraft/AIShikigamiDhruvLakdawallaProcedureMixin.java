package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIShikigamiDhruvLakdawallaProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AIShikigamiDhruvLakdawallaProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AIShikigamiDhruvLakdawallaProcedure.class, remap = false)
public abstract class AIShikigamiDhruvLakdawallaProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$enforceDhruvOwnerRange(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        AIShikigamiDhruvLakdawallaProcedureHook.enforceOwnerRange(world, entity);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/GetDistanceNearestEnemyProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)D"
        ),
        remap = false,
        require = 1
    )
    private static double jja$alwaysPassDhruvAggroPlacementCheck(
        LevelAccessor world,
        Entity placementEntity,
        Operation<Double> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return AIShikigamiDhruvLakdawallaProcedureHook.resolvePlacementDistance(
            entity,
            () -> original.call(world, placementEntity)
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;random()D",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static double jja$alwaysPassDhruvIdlePlacementCheck(Operation<Double> original, @Local(argsOnly = true) Entity entity) {
        return AIShikigamiDhruvLakdawallaProcedureHook.resolveIdlePlacementRoll(entity, original::call);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$preserveDhruvTrailLifetime(
        Commands commands,
        CommandSourceStack commandSourceStack,
        String command,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return AIShikigamiDhruvLakdawallaProcedureHook.preserveTrailLifetime(
            commands,
            commandSourceStack,
            command,
            entity,
            original
        );
    }
}
