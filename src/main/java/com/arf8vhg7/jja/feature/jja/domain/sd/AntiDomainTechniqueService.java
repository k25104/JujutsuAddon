package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupService;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class AntiDomainTechniqueService {
    private static final String HOLLOW_WICKER_BASKET_TRANSLATION_KEY = "advancements.mastery_hwb.title";
    private static final String SIMPLE_DOMAIN_TRANSLATION_KEY = "effect.simple_domain";
    private static final String FALLING_BLOSSOM_EMOTION_TRANSLATION_KEY = "effect.jujutsucraft.falling_blossom_emotion";

    private AntiDomainTechniqueService() {
    }

    public static AntiDomainTechniqueOption getCurrentSelection(Entity entity) {
        if (entity == null) {
            return AntiDomainTechniqueOption.NONE;
        }
        return TechniqueSetupService.resolveCurrentAntiDomainOption(entity);
    }

    public static AntiDomainPresentation getActivePresentation(Entity entity) {
        if (entity == null) {
            return AntiDomainPresentation.NONE;
        }
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            return AntiDomainPresentation.NONE;
        }
        return AntiDomainPresentation.fromId(addonStats.getActiveAntiDomainPresentationId());
    }

    public static AntiDomainPresentation resolvePlayerPresentation(Entity entity) {
        if (!(entity instanceof Player)) {
            return AntiDomainPresentation.NONE;
        }
        return resolveTechniqueSetupPresentation(
            AntiDomainRuntimeStore.getRuntimeActivePresentation(entity),
            getActivePresentation(entity),
            AntiDomainRuntimeStore.getRuntimeSelectedOptionAtPress(entity),
            AntiDomainTechniqueRules.hasOwnedSimpleDomain(entity),
            getCurrentSelection(entity)
        );
    }

    public static AntiDomainPresentation resolveActivePresentation(Entity entity) {
        if (entity == null) {
            return AntiDomainPresentation.NONE;
        }
        return resolveActivePresentation(
            AntiDomainRuntimeStore.getRuntimeActivePresentation(entity),
            getActivePresentation(entity),
            entity instanceof Player ? AntiDomainRuntimeStore.getRuntimeSelectedOptionAtPress(entity) : null,
            AntiDomainTechniqueRules.hasOwnedSimpleDomain(entity)
        );
    }

    public static boolean hasOwnedSimpleDomain(Entity entity) {
        return AntiDomainTechniqueRules.hasOwnedSimpleDomain(entity);
    }

    public static boolean canUseHollowWickerBasket(Entity entity) {
        return AntiDomainTechniqueRules.canUseHollowWickerBasket(entity);
    }

    public static boolean shouldUseHwbVisual(Entity entity, boolean original) {
        return shouldUseHwbVisual(
            entity instanceof Player,
            original,
            resolvePlayerPresentation(entity),
            AntiDomainTechniqueRules.isNonPlayerHollowWickerBasket(entity)
        );
    }

    public static boolean shouldUseActiveHwbVisual(Entity entity, boolean original) {
        return shouldUseHwbVisual(
            entity instanceof Player,
            original,
            resolveActivePresentation(entity),
            AntiDomainTechniqueRules.isNonPlayerHollowWickerBasket(entity)
        );
    }

    public static boolean shouldSuppressSimpleDomainDerivedEffects(Entity entity) {
        return hasActiveHollowWickerBasket(entity);
    }

    public static boolean shouldSuppressActivePlayerEffects(Entity entity) {
        return shouldSuppressSimpleDomainDerivedEffects(entity);
    }

    public static boolean hasActiveHollowWickerBasket(Entity entity) {
        return AntiDomainTechniqueRules.hasActiveHollowWickerBasket(entity, resolveActivePresentation(entity));
    }

    public static void latchActivePresentation(Entity entity) {
        latchActivePresentation(entity, resolveSelectionPresentation(getCurrentSelection(entity)));
    }

    public static void latchActivePresentation(Entity entity, AntiDomainPresentation presentation) {
        if (entity == null) {
            return;
        }
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            return;
        }
        addonStats.setActiveAntiDomainPresentationId((presentation != null ? presentation : AntiDomainPresentation.NONE).id());
    }

    public static void latchActivePresentation(Entity entity, AntiDomainTechniqueOption option) {
        if (option == AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION) {
            return;
        }
        latchActivePresentation(entity, resolveSelectionPresentation(option));
    }

    public static void clearActivePresentation(Entity entity) {
        if (entity == null) {
            return;
        }
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            return;
        }
        addonStats.setActiveAntiDomainPresentationId(AntiDomainPresentation.NONE.id());
    }

    public static boolean shouldAutoExtendHollowWickerBasket(Entity entity) {
        return AntiDomainTechniqueRules.shouldAutoExtendHollowWickerBasket(entity, resolveActivePresentation(entity));
    }

    public static boolean shouldPreserveCounterAntiDomainEffects(Entity entity) {
        return AntiDomainTechniqueRules.shouldPreserveCounterAntiDomainEffects(entity);
    }

    public static double capSimpleDomainRadius(double radiusBeforeCap) {
        return AntiDomainTechniqueRules.capSimpleDomainRadius(radiusBeforeCap);
    }

    public static MutableComponent getResolvedSimpleDomainTechniqueName(Entity entity) {
        AntiDomainPresentation presentation = resolvePlayerPresentation(entity);
        if (presentation != AntiDomainPresentation.NONE) {
            return getPresentationTechniqueName(presentation);
        }
        AntiDomainTechniqueOption option = getCurrentSelection(entity);
        if (option == AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION) {
            option = AntiDomainTechniqueOption.SIMPLE_DOMAIN;
        }
        return getTechniqueName(option);
    }

    public static MutableComponent getLastActivatedSimpleDomainTechniqueName(Entity entity) {
        return getPresentationTechniqueName(
            resolveLastActivatedSimpleDomainPresentation(
                AntiDomainRuntimeStore.getRuntimeActivePresentation(entity),
                getActivePresentation(entity),
                AntiDomainRuntimeStore.getRuntimeSelectedOptionAtPress(entity)
            )
        );
    }

    public static MutableComponent getTechniqueName(AntiDomainTechniqueOption option) {
        return switch (option) {
            case NONE -> Component.translatable("key.keyboard.unknown");
            case HOLLOW_WICKER_BASKET -> Component.translatable(HOLLOW_WICKER_BASKET_TRANSLATION_KEY);
            case FALLING_BLOSSOM_EMOTION -> Component.translatable(FALLING_BLOSSOM_EMOTION_TRANSLATION_KEY);
            case SIMPLE_DOMAIN -> Component.translatable(SIMPLE_DOMAIN_TRANSLATION_KEY);
        };
    }

    public static MutableComponent getPresentationTechniqueName(AntiDomainPresentation presentation) {
        return switch (presentation) {
            case HOLLOW_WICKER_BASKET -> Component.translatable(HOLLOW_WICKER_BASKET_TRANSLATION_KEY);
            case SIMPLE_DOMAIN, NONE -> Component.translatable(SIMPLE_DOMAIN_TRANSLATION_KEY);
        };
    }

    static AntiDomainPresentation resolveLastActivatedSimpleDomainPresentation(
        AntiDomainPresentation runtimePresentation,
        AntiDomainPresentation latchedPresentation,
        AntiDomainTechniqueOption pressedOptionAtPress
    ) {
        return AntiDomainPresentationResolver.resolveLastActivated(runtimePresentation, latchedPresentation, pressedOptionAtPress);
    }

    static AntiDomainPresentation resolveTechniqueSetupPresentation(
        AntiDomainPresentation runtimePresentation,
        AntiDomainPresentation latchedPresentation,
        AntiDomainTechniqueOption pressedOptionAtPress,
        boolean ownsSimpleDomain,
        AntiDomainTechniqueOption currentSelection
    ) {
        return AntiDomainPresentationResolver.resolveTechniqueSetupPresentation(
            runtimePresentation,
            latchedPresentation,
            pressedOptionAtPress,
            ownsSimpleDomain,
            currentSelection
        );
    }

    static AntiDomainPresentation resolveActivePresentation(
        AntiDomainPresentation runtimePresentation,
        AntiDomainPresentation latchedPresentation,
        AntiDomainTechniqueOption pressedOptionAtPress,
        boolean ownsSimpleDomain
    ) {
        return AntiDomainPresentationResolver.resolveActive(
            runtimePresentation,
            latchedPresentation,
            pressedOptionAtPress,
            ownsSimpleDomain
        );
    }

    static AntiDomainPresentation resolveSelectionPresentation(AntiDomainTechniqueOption option) {
        return AntiDomainPresentationResolver.resolveSelection(option);
    }

    static boolean shouldUseHwbVisual(
        boolean playerControlled,
        boolean original,
        AntiDomainPresentation presentation,
        boolean nonPlayerHollowWickerBasket
    ) {
        return hasActiveHollowWickerBasket(playerControlled, presentation, nonPlayerHollowWickerBasket) || (!playerControlled && original);
    }

    static boolean shouldSuppressSimpleDomainDerivedEffects(
        boolean playerControlled,
        AntiDomainPresentation presentation,
        boolean nonPlayerHollowWickerBasket
    ) {
        return hasActiveHollowWickerBasket(playerControlled, presentation, nonPlayerHollowWickerBasket);
    }

    static boolean hasActiveHollowWickerBasket(
        boolean playerControlled,
        AntiDomainPresentation presentation,
        boolean nonPlayerHollowWickerBasket
    ) {
        return AntiDomainTechniqueRules.hasActiveHollowWickerBasket(playerControlled, presentation, nonPlayerHollowWickerBasket);
    }

    static boolean shouldSuppressPlayerEffects(boolean playerControlled, AntiDomainPresentation presentation) {
        return shouldSuppressSimpleDomainDerivedEffects(playerControlled, presentation, false);
    }

    static boolean canUseHollowWickerBasket(boolean hasSukunaEffect, boolean masteredHollowWickerBasket) {
        return AntiDomainTechniqueRules.canUseHollowWickerBasket(hasSukunaEffect, masteredHollowWickerBasket);
    }

    static boolean shouldPreserveCounterAntiDomainEffects(
        boolean grade2Qualified,
        boolean zeninOgiEntity,
        boolean kusakabeAtsuyaEntity
    ) {
        return AntiDomainTechniqueRules.shouldPreserveCounterAntiDomainEffects(grade2Qualified, zeninOgiEntity, kusakabeAtsuyaEntity);
    }
}
