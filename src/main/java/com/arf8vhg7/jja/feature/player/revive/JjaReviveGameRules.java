package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
public final class JjaReviveGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> JJA_REVIVE =
        GameRules.register("jjaRevive", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));

    private JjaReviveGameRules() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // no-op: class load registers the game rule
    }
}
