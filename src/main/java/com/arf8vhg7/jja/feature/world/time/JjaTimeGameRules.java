package com.arf8vhg7.jja.feature.world.time;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
public final class JjaTimeGameRules {
    public static final String JJA_NIGHT_TIME_SPEED_RULE = "jjaNightTimeSpeed";
    public static final String JJA_DAY_TIME_SPEED_RULE = "jjaDayTimeSpeed";
    public static final GameRules.Key<GameRules.IntegerValue> JJA_NIGHT_TIME_SPEED =
        GameRules.register(JJA_NIGHT_TIME_SPEED_RULE, GameRules.Category.UPDATES, GameRules.IntegerValue.create(100));
    public static final GameRules.Key<GameRules.IntegerValue> JJA_DAY_TIME_SPEED =
        GameRules.register(JJA_DAY_TIME_SPEED_RULE, GameRules.Category.UPDATES, GameRules.IntegerValue.create(100));

    private JjaTimeGameRules() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // no-op: class load registers game rules
    }
}
