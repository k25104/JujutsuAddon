package com.arf8vhg7.jja.feature.jja.technique.shared.effect;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class JjaMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JujutsuAddon.MODID);
    public static final RegistryObject<MobEffect> COOLDOWN_TIME_TELEPORT = MOB_EFFECTS.register(
        "cooldown_time_teleport",
        TeleportCooldownMobEffect::new
    );

    private JjaMobEffects() {
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
