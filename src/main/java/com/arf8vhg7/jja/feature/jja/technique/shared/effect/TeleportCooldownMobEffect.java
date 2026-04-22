package com.arf8vhg7.jja.feature.jja.technique.shared.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class TeleportCooldownMobEffect extends MobEffect {
    private static final int TELEPORT_COOLDOWN_COLOR = 0x455A70;

    public TeleportCooldownMobEffect() {
        super(MobEffectCategory.HARMFUL, TELEPORT_COOLDOWN_COLOR);
    }
}
