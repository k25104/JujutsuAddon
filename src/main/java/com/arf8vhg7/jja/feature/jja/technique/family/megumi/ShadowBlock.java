package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.JujutsuBarrierBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

public final class ShadowBlock extends JujutsuBarrierBase implements EntityBlock {
    public ShadowBlock() {
        super(
            BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(-1.0F, 9999.0F)
                .lightLevel(state -> 15)
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn((state, level, pos, entityType) -> true)
                .isRedstoneConductor((state, level, pos) -> true)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (MegumiShadowService.canEntityPassThroughShadow(level, pos, entity)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, Mob entity) {
        return BlockPathTypes.OPEN;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction direction, IPlantable plantable) {
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        MegumiShadowService.onEntityInsideShadow(level, pos, entity);
    }

    @Override
    public InteractionResult use(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return MegumiShadowService.tryOpenStorage(level, pos, player) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShadowBlockEntity(pos, state);
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        if (type != MegumiShadowBlocks.SHADOW_BLOCK_ENTITY.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) -> MegumiShadowService.tickShadowBlock(
            tickerLevel,
            pos,
            tickerState,
            (ShadowBlockEntity) blockEntity
        );
    }
}
