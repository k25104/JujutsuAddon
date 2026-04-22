package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeTier;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import net.mcreator.jujutsucraft.entity.KusakabeAtsuyaEntity;
import net.mcreator.jujutsucraft.entity.ZeninOgiEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

final class AntiDomainTechniqueRules {
    private static final ResourceLocation CAN_USE_HOLLOW_WICKER_BASKET_TAG_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "can_use_hollow_wicker_basket"
    );

    private AntiDomainTechniqueRules() {
    }

    static boolean hasOwnedSimpleDomain(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        return AntiDomainTechniqueService.getActivePresentation(entity) == AntiDomainPresentation.SIMPLE_DOMAIN;
    }

    static boolean canUseHollowWickerBasket(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        LivingEntity livingEntity = player;
        return canUseHollowWickerBasket(
            livingEntity.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get()),
            player instanceof ServerPlayer serverPlayer && JjaAdvancementHelper.has(serverPlayer, HollowWickerBasketProgression.MASTERY_ID)
        );
    }

    static boolean canUseHollowWickerBasket(boolean hasSukunaEffect, boolean masteredHollowWickerBasket) {
        return hasSukunaEffect || masteredHollowWickerBasket;
    }

    static boolean shouldAutoExtendHollowWickerBasket(Entity entity, AntiDomainPresentation activePresentation) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        if (!hasOwnedSimpleDomain(entity) || activePresentation != AntiDomainPresentation.HOLLOW_WICKER_BASKET) {
            return false;
        }
        return true;
    }

    static boolean shouldPreserveCounterAntiDomainEffects(Entity entity) {
        return shouldPreserveCounterAntiDomainEffects(
            entity instanceof ServerPlayer player && JjaAdvancementHelper.has(player, SorcererGradeTier.GRADE_2.advancementId()),
            entity instanceof ZeninOgiEntity,
            entity instanceof KusakabeAtsuyaEntity
        );
    }

    static boolean shouldPreserveCounterAntiDomainEffects(
        boolean grade2Qualified,
        boolean zeninOgiEntity,
        boolean kusakabeAtsuyaEntity
    ) {
        return grade2Qualified || zeninOgiEntity || kusakabeAtsuyaEntity;
    }

    static double capSimpleDomainRadius(double radiusBeforeCap) {
        return Math.min(16.0D, radiusBeforeCap);
    }

    static boolean hasActiveHollowWickerBasket(Entity entity, AntiDomainPresentation activePresentation) {
        return hasActiveHollowWickerBasket(entity instanceof Player, activePresentation, isNonPlayerHollowWickerBasket(entity));
    }

    static boolean hasActiveHollowWickerBasket(boolean playerControlled, AntiDomainPresentation activePresentation, boolean nonPlayerHollowWickerBasket) {
        return playerControlled ? activePresentation == AntiDomainPresentation.HOLLOW_WICKER_BASKET : nonPlayerHollowWickerBasket;
    }

    static boolean isNonPlayerHollowWickerBasket(Entity entity) {
        if (entity instanceof Player || !(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        return entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, CAN_USE_HOLLOW_WICKER_BASKET_TAG_ID))
            || livingEntity.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get());
    }
}
