package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AttackWeakPunchProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.AttackWeakPunchProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttackWeakPunchProcedure.class, remap = false)
public abstract class AttackWeakPunchProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;m_262451_(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/nbt/CompoundTag;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;ZZ)Lnet/minecraft/world/entity/Entity;"
        ),
        remap = false,
        require = 1
    )
    private static @Nullable Entity jja$replaceAttackStrikeEntity(
        EntityType<?> entityType,
        ServerLevel level,
        @Nullable CompoundTag tag,
        @Nullable Consumer<?> consumer,
        BlockPos blockPos,
        MobSpawnType mobSpawnType,
        boolean alignToSurface,
        boolean invertY,
        Operation<Entity> original,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "x_pos") double xPos,
        @Local(name = "y_pos") double yPos,
        @Local(name = "z_pos") double zPos,
        @Local(name = "range") double range,
        @Local(name = "combo") boolean combo
    ) {
        return AttackWeakPunchProcedureHook.replaceAttackStrikeEntity(level, entity, xPos, yPos, zPos, range, combo);
    }
}
