package com.arf8vhg7.jja;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.combat.strike.JjaAttackStrikeParticles;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainBlocks;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowBlocks;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowMenus;
import com.arf8vhg7.jja.feature.jja.technique.shared.effect.JjaMobEffects;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JujutsuAddon.MODID)
public final class JujutsuAddon {
    public static final String MODID = "jja";

    public JujutsuAddon(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, JjaCommonConfig.SPEC);
        JjaAttackStrikeParticles.register(context.getModEventBus());
        CurtainBlocks.register(context.getModEventBus());
        MegumiShadowBlocks.register(context.getModEventBus());
        MegumiShadowMenus.register(context.getModEventBus());
        JjaMobEffects.register(context.getModEventBus());
    }
}
