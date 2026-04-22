package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CurtainBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JujutsuAddon.MODID);
    public static final RegistryObject<Block> CURTAIN_SHELL = BLOCKS.register("curtain_shell", CurtainShellBlock::new);

    private CurtainBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
