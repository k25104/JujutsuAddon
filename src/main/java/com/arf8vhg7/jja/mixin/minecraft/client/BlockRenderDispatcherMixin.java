package com.arf8vhg7.jja.mixin.minecraft.client;

import com.arf8vhg7.jja.hook.minecraft.client.BlockRenderDispatcherHook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin {
    @Inject(
        method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private void jja$skipCurtainShellWorldRender(
        BlockState state,
        BlockPos pos,
        BlockAndTintGetter level,
        PoseStack poseStack,
        VertexConsumer consumer,
        boolean checkSides,
        RandomSource random,
        CallbackInfo ci
    ) {
        if (BlockRenderDispatcherHook.shouldSkipCurtainShellRender(state)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "renderSingleBlock(Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private void jja$skipCurtainShellSingleRender(
        BlockState state,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay,
        CallbackInfo ci
    ) {
        if (BlockRenderDispatcherHook.shouldSkipCurtainShellRender(state)) {
            ci.cancel();
        }
    }
}
