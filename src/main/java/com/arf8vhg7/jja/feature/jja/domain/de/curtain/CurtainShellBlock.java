package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CurtainShellBlock extends Block {
    public CurtainShellBlock() {
        super(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(-1.0F, 3600000.0F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .noLootTable()
                .pushReaction(PushReaction.BLOCK)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
        );
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (CurtainRuntimeService.canEntityPassThroughShell(level, pos, entity)) {
                if (entity != null && entity.getY() > pos.getY() + 0.9D) {
                    return super.getCollisionShape(state, level, pos, context);
                }
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }
}
