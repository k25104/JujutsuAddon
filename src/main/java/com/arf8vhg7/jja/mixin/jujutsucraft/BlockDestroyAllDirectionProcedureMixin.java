package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.BlockDestroyAllDirectionProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.BlockDestroyAllDirectionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockDestroyAllDirectionProcedure.class, remap = false)
public abstract class BlockDestroyAllDirectionProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;m_204336_(Lnet/minecraft/tags/TagKey;)Z",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$resolveCurtainBarrierBreakabilityEarly(
        BlockState state,
        TagKey<Block> tagKey,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity,
        @Local BlockPos currentPos
    ) {
        return BlockDestroyAllDirectionProcedureHook.shouldTreatAsBreakableBarrier(
            original.call(state, tagKey),
            state,
            tagKey,
            world,
            currentPos,
            entity
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;m_204336_(Lnet/minecraft/tags/TagKey;)Z",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$resolveCurtainBarrierBreakabilitySurrounding(
        BlockState state,
        TagKey<Block> tagKey,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity,
        @Local BlockPos currentPos
    ) {
        return BlockDestroyAllDirectionProcedureHook.shouldTreatAsBreakableBarrier(
            original.call(state, tagKey),
            state,
            tagKey,
            world,
            currentPos,
            entity
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;m_204336_(Lnet/minecraft/tags/TagKey;)Z",
            ordinal = 8
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$resolveCurtainBarrierBreakabilityDestroy(
        BlockState state,
        TagKey<Block> tagKey,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity,
        @Local BlockPos currentPos
    ) {
        return BlockDestroyAllDirectionProcedureHook.shouldTreatAsBreakableBarrier(
            original.call(state, tagKey),
            state,
            tagKey,
            world,
            currentPos,
            entity
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;m_204336_(Lnet/minecraft/tags/TagKey;)Z",
            ordinal = 9
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$resolveCurtainBarrierBreakabilityFragment(
        BlockState state,
        TagKey<Block> tagKey,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity,
        @Local BlockPos currentPos
    ) {
        return BlockDestroyAllDirectionProcedureHook.shouldTreatAsBreakableBarrier(
            original.call(state, tagKey),
            state,
            tagKey,
            world,
            currentPos,
            entity
        );
    }
}
