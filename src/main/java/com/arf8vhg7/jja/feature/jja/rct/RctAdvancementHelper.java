package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.Objects;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;

public final class RctAdvancementHelper {
    public static final ResourceLocation RCT_1_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "reverse_cursed_technique_1");
    public static final ResourceLocation RCT_2_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "reverse_cursed_technique_2");
    public static final ResourceLocation MASTERY_RCT_OUTPUT_ID = ResourceLocation.fromNamespaceAndPath("jja", "mastery_rct_output");
    public static final ResourceLocation MASTERY_RCT_AUTO_ID = ResourceLocation.fromNamespaceAndPath("jja", "mastery_rct_auto");
    public static final ResourceLocation MASTERY_RCT_BRAIN_DESTRUCTION_ID = ResourceLocation.fromNamespaceAndPath("jja", "mastery_rct_brain_destruction");
    public static final ResourceLocation MASTERY_RCT_BRAIN_REGENERATION_ID = ResourceLocation.fromNamespaceAndPath("jja", "mastery_rct_brain_regeneration");

    private RctAdvancementHelper() {
    }

    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        return JjaAdvancementHelper.has(player, advancementId);
    }

    public static boolean hasAdvancement(Entity entity, ResourceLocation advancementId) {
        return entity instanceof ServerPlayer player && hasAdvancement(player, advancementId);
    }

    public static boolean hasAdvancementOrSukunaEffect(ServerPlayer player, ResourceLocation advancementId) {
        MobEffect sukunaEffect = Objects.requireNonNull(JujutsucraftModMobEffects.SUKUNA_EFFECT.get());
        return player != null
            && (
                hasAdvancement(player, advancementId)
                    || player.hasEffect(sukunaEffect)
            );
    }

    public static boolean award(ServerPlayer player, ResourceLocation advancementId) {
        return JjaAdvancementHelper.award(player, advancementId);
    }
}
