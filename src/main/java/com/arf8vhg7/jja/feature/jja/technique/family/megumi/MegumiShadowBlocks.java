package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MegumiShadowBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JujutsuAddon.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
        ForgeRegistries.BLOCK_ENTITY_TYPES,
        JujutsuAddon.MODID
    );

    public static final RegistryObject<Block> SHADOW_BLOCK = BLOCKS.register("shadow_block", ShadowBlock::new);
    public static final RegistryObject<BlockEntityType<ShadowBlockEntity>> SHADOW_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
        "shadow_block",
        () -> BlockEntityType.Builder.of(ShadowBlockEntity::new, SHADOW_BLOCK.get()).build(null)
    );

    private MegumiShadowBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
