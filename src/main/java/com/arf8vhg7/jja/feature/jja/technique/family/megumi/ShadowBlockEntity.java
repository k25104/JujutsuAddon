package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ShadowBlockEntity extends BlockEntity {
    private static final String KEY_OWNER = "owner";
    private static final String KEY_ORIGINAL_STATE = "original_state";
    private static final String KEY_ORIGINAL_BLOCK_ENTITY = "original_block_entity";
    private static final String KEY_LAST_OWNER_TOUCH = "last_owner_touch";

    @Nullable
    private UUID ownerId;
    private BlockState originalState = Blocks.AIR.defaultBlockState();
    @Nullable
    private CompoundTag originalBlockEntityTag;
    private long lastOwnerTouchGameTime;
    private int storageOpenCount;

    public ShadowBlockEntity(BlockPos pos, BlockState state) {
        super(MegumiShadowBlocks.SHADOW_BLOCK_ENTITY.get(), pos, state);
    }

    void initialize(UUID ownerId, BlockState originalState, @Nullable CompoundTag originalBlockEntityTag, long gameTime) {
        this.ownerId = ownerId;
        this.originalState = originalState;
        this.originalBlockEntityTag = originalBlockEntityTag == null ? null : originalBlockEntityTag.copy();
        this.lastOwnerTouchGameTime = gameTime;
        this.storageOpenCount = 0;
        this.setChanged();
    }

    @Nullable
    UUID ownerId() {
        return this.ownerId;
    }

    BlockState originalState() {
        return this.originalState;
    }

    @Nullable
    CompoundTag originalBlockEntityTag() {
        return this.originalBlockEntityTag == null ? null : this.originalBlockEntityTag.copy();
    }

    long lastOwnerTouchGameTime() {
        return this.lastOwnerTouchGameTime;
    }

    void markOwnerTouch(long gameTime) {
        if (this.lastOwnerTouchGameTime != gameTime) {
            this.lastOwnerTouchGameTime = gameTime;
            this.setChanged();
        }
    }

    void startStorageOpen() {
        this.storageOpenCount++;
        this.setChanged();
    }

    void stopStorageOpen() {
        if (this.storageOpenCount > 0) {
            this.storageOpenCount--;
            this.setChanged();
        }
    }

    boolean hasOpenStorage() {
        return this.storageOpenCount > 0;
    }

    boolean isOwnedBy(UUID ownerId) {
        return ownerId.equals(this.ownerId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.ownerId = tag.hasUUID(KEY_OWNER) ? tag.getUUID(KEY_OWNER) : null;
        this.originalState = tag.contains(KEY_ORIGINAL_STATE, Tag.TAG_COMPOUND)
            ? NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound(KEY_ORIGINAL_STATE))
            : Blocks.AIR.defaultBlockState();
        this.originalBlockEntityTag = tag.contains(KEY_ORIGINAL_BLOCK_ENTITY, Tag.TAG_COMPOUND)
            ? tag.getCompound(KEY_ORIGINAL_BLOCK_ENTITY).copy()
            : null;
        this.lastOwnerTouchGameTime = tag.getLong(KEY_LAST_OWNER_TOUCH);
        this.storageOpenCount = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerId != null) {
            tag.putUUID(KEY_OWNER, this.ownerId);
        }
        tag.put(KEY_ORIGINAL_STATE, NbtUtils.writeBlockState(this.originalState));
        if (this.originalBlockEntityTag != null) {
            tag.put(KEY_ORIGINAL_BLOCK_ENTITY, this.originalBlockEntityTag.copy());
        }
        tag.putLong(KEY_LAST_OWNER_TOUCH, this.lastOwnerTouchGameTime);
    }
}
