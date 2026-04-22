package com.arf8vhg7.jja.feature.jja.technique.shared.menu;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainTypeOption;
import com.arf8vhg7.jja.feature.jja.domain.de.OpenBarrierUnlockRules;
import com.arf8vhg7.jja.feature.jja.domain.fbe.FallingBlossomEmotionProgression;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import com.arf8vhg7.jja.feature.jja.technique.shared.registration.JjaSkillManagementProbe;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;

public final class TechniqueSetupService {
    public static final ResourceLocation MASTERY_HOLLOW_WICKER_BASKET_ID = ResourceLocation.fromNamespaceAndPath(
        "jja",
        "mastery_hollow_wicker_basket"
    );
    public static final ResourceLocation MASTERY_SIMPLE_DOMAIN_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "mastery_simple_domain");
    public static final ResourceLocation MASTERY_DOMAIN_EXPANSION_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_domain_expansion"
    );
    public static final ResourceLocation MASTERY_OPEN_BARRIER_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_open_barrier_type_domain"
    );

    private TechniqueSetupService() {
    }

    public static boolean ensureInitialized(Player player) {
        return player instanceof ServerPlayer serverPlayer && ensureInitialized(serverPlayer);
    }

    public static boolean ensureInitialized(ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        if (addonStats == null) {
            return false;
        }

        boolean changed = false;
        if (!addonStats.isTechniqueSetupMigrated()) {
            AntiDomainTechniqueOption legacyNormal = addonStats.isHollowWickerBasketEnabled()
                ? AntiDomainTechniqueOption.HOLLOW_WICKER_BASKET
                : AntiDomainTechniqueOption.SIMPLE_DOMAIN;
            AntiDomainTechniqueOption legacyCrouch = FallingBlossomEmotionProgression.hasUnlocked(player)
                ? AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION
                : legacyNormal;
            addonStats.setAntiDomainNormalOptionId(legacyNormal.id());
            addonStats.setAntiDomainCrouchOptionId(legacyCrouch.id());
            addonStats.setDomainTypeNormalOptionId(DomainTypeOption.OPEN_BARRIER.id());
            addonStats.setDomainTypeCrouchOptionId(DomainTypeOption.BARRIER.id());
            addonStats.setActiveAntiDomainPresentationId(AntiDomainPresentation.NONE.id());
            addonStats.setTechniqueSetupMigrated(true);
            changed = true;
        }

        TechniqueSetupAvailability availability = resolveAvailability(player);
        int newlyAvailableAntiDomainMask = newlyAvailableMask(
            availability.antiDomainAvailableMask(),
            addonStats.getSeenAntiDomainAvailableMask()
        );
        AntiDomainTechniqueOption normalizedNormal = resolveInitializedAntiDomainOption(
            addonStats.getAntiDomainNormalOptionId(),
            availability.antiDomainAvailableMask(),
            newlyAvailableAntiDomainMask,
            TechniqueSetupInputSlot.NORMAL,
            addonStats.isHollowWickerBasketEnabled()
        );
        if (addonStats.getAntiDomainNormalOptionId() != normalizedNormal.id()) {
            addonStats.setAntiDomainNormalOptionId(normalizedNormal.id());
            changed = true;
        }

        AntiDomainTechniqueOption normalizedCrouch = resolveInitializedAntiDomainOption(
            addonStats.getAntiDomainCrouchOptionId(),
            availability.antiDomainAvailableMask(),
            newlyAvailableAntiDomainMask,
            TechniqueSetupInputSlot.CROUCH,
            addonStats.isHollowWickerBasketEnabled()
        );
        if (addonStats.getAntiDomainCrouchOptionId() != normalizedCrouch.id()) {
            addonStats.setAntiDomainCrouchOptionId(normalizedCrouch.id());
            changed = true;
        }

        int newlyAvailableDomainTypeMask = newlyAvailableMask(
            availability.domainTypeAvailableMask(),
            addonStats.getSeenDomainTypeAvailableMask()
        );
        DomainTypeOption normalizedDomainNormal = resolveInitializedDomainTypeOption(
            addonStats.getDomainTypeNormalOptionId(),
            availability.domainTypeAvailableMask(),
            newlyAvailableDomainTypeMask,
            TechniqueSetupInputSlot.NORMAL
        );
        if (addonStats.getDomainTypeNormalOptionId() != normalizedDomainNormal.id()) {
            addonStats.setDomainTypeNormalOptionId(normalizedDomainNormal.id());
            changed = true;
        }

        DomainTypeOption normalizedDomainCrouch = resolveInitializedDomainTypeOption(
            addonStats.getDomainTypeCrouchOptionId(),
            availability.domainTypeAvailableMask(),
            newlyAvailableDomainTypeMask,
            TechniqueSetupInputSlot.CROUCH
        );
        if (addonStats.getDomainTypeCrouchOptionId() != normalizedDomainCrouch.id()) {
            addonStats.setDomainTypeCrouchOptionId(normalizedDomainCrouch.id());
            changed = true;
        }

        int seenAntiDomainMask = addonStats.getSeenAntiDomainAvailableMask() | availability.antiDomainAvailableMask();
        if (addonStats.getSeenAntiDomainAvailableMask() != seenAntiDomainMask) {
            addonStats.setSeenAntiDomainAvailableMask(seenAntiDomainMask);
            changed = true;
        }

        int seenDomainTypeMask = addonStats.getSeenDomainTypeAvailableMask() | availability.domainTypeAvailableMask();
        if (addonStats.getSeenDomainTypeAvailableMask() != seenDomainTypeMask) {
            addonStats.setSeenDomainTypeAvailableMask(seenDomainTypeMask);
            changed = true;
        }

        AntiDomainPresentation activePresentation = AntiDomainPresentation.fromId(addonStats.getActiveAntiDomainPresentationId());
        if (!hasOwnedSimpleDomain(player) && activePresentation != AntiDomainPresentation.NONE) {
            addonStats.setActiveAntiDomainPresentationId(AntiDomainPresentation.NONE.id());
            changed = true;
        }

        return changed;
    }

    public static TechniqueSetupAvailability resolveAvailability(ServerPlayer player) {
        boolean simpleDomainUnlocked = JjaAdvancementHelper.has(player, MASTERY_SIMPLE_DOMAIN_ID);
        boolean hollowWickerBasketUnlocked = hasSukunaEffect(player) || JjaAdvancementHelper.has(player, MASTERY_HOLLOW_WICKER_BASKET_ID);
        boolean fbeUnlocked = FallingBlossomEmotionProgression.hasUnlocked(player);
        int antiDomainCount = (simpleDomainUnlocked ? 1 : 0) + (hollowWickerBasketUnlocked ? 1 : 0) + (fbeUnlocked ? 1 : 0);
        int antiDomainAvailableMask = 0;
        if (simpleDomainUnlocked) {
            antiDomainAvailableMask |= AntiDomainTechniqueOption.SIMPLE_DOMAIN.mask();
        }
        if (hollowWickerBasketUnlocked) {
            antiDomainAvailableMask |= AntiDomainTechniqueOption.HOLLOW_WICKER_BASKET.mask();
        }
        if (fbeUnlocked) {
            antiDomainAvailableMask |= AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION.mask();
        }

        boolean domainExpansionUnlocked = JjaAdvancementHelper.has(player, MASTERY_DOMAIN_EXPANSION_ID);
        boolean openBarrierUnlocked = OpenBarrierUnlockRules.hasAccess(
            hasSukunaEffect(player),
            JjaAdvancementHelper.has(player, MASTERY_OPEN_BARRIER_ID)
        );
        int visibleCategoriesMask = 0;
        if (antiDomainCount >= 1) {
            visibleCategoriesMask |= TechniqueSetupCategory.ANTI_DOMAIN.mask();
        }
        if (OpenBarrierUnlockRules.isDomainTypeVisible(domainExpansionUnlocked, openBarrierUnlocked)) {
            visibleCategoriesMask |= TechniqueSetupCategory.DOMAIN_TYPE.mask();
        }

        int domainTypeAvailableMask = OpenBarrierUnlockRules.resolveDomainTypeAvailableMask(domainExpansionUnlocked, openBarrierUnlocked);

        return new TechniqueSetupAvailability(visibleCategoriesMask, antiDomainAvailableMask, domainTypeAvailableMask);
    }

    public static TechniqueSetupViewState buildViewState(ServerPlayer player) {
        ensureInitialized(player);
        TechniqueSetupAvailability availability = resolveAvailability(player);
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        if (addonStats == null) {
            return new TechniqueSetupViewState(0, 0, 0, 0, 0, 0, 0, List.of());
        }
        return new TechniqueSetupViewState(
            availability.visibleCategoriesMask(),
            availability.antiDomainAvailableMask(),
            availability.domainTypeAvailableMask(),
            addonStats.getAntiDomainNormalOptionId(),
            addonStats.getAntiDomainCrouchOptionId(),
            addonStats.getDomainTypeNormalOptionId(),
            addonStats.getDomainTypeCrouchOptionId(),
            JjaSkillManagementProbe.collectRegistrationCandidates(player)
        );
    }

    public static boolean cycle(ServerPlayer player, TechniqueSetupCategory category, TechniqueSetupInputSlot slot) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        if (addonStats == null) {
            return false;
        }
        ensureInitialized(player);
        TechniqueSetupAvailability availability = resolveAvailability(player);
        if (!availability.isVisible(category)) {
            return false;
        }

        if (category == TechniqueSetupCategory.DOMAIN_TYPE) {
            DomainTypeOption next = DomainTypeOption.nextAvailable(getDomainTypeId(addonStats, slot), availability.domainTypeAvailableMask());
            return setDomainTypeId(addonStats, slot, next.id());
        }

        AntiDomainTechniqueOption next = AntiDomainTechniqueOption.nextAvailable(
            getAntiDomainOptionId(addonStats, slot),
            availability.antiDomainAvailableMask()
        );
        return setAntiDomainOptionId(addonStats, slot, next.id());
    }

    public static AntiDomainTechniqueOption resolveCurrentAntiDomainOption(Entity entity) {
        return resolveAntiDomainOption(entity, TechniqueSetupInputSlot.fromEntity(entity));
    }

    public static AntiDomainTechniqueOption resolveAntiDomainOption(Entity entity, TechniqueSetupInputSlot slot) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            return AntiDomainTechniqueOption.NONE;
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            ensureInitialized(serverPlayer);
            return AntiDomainTechniqueOption.normalize(
                getAntiDomainOptionId(addonStats, slot),
                resolveAvailability(serverPlayer).antiDomainAvailableMask()
            );
        }
        return AntiDomainTechniqueOption.fromId(getAntiDomainOptionId(addonStats, slot));
    }

    public static DomainTypeOption resolveCurrentDomainType(Entity entity) {
        return resolveDomainType(entity, TechniqueSetupInputSlot.fromEntity(entity));
    }

    public static DomainTypeOption resolveDomainType(Entity entity, TechniqueSetupInputSlot slot) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            return slot == TechniqueSetupInputSlot.CROUCH ? DomainTypeOption.BARRIER : DomainTypeOption.OPEN_BARRIER;
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            ensureInitialized(serverPlayer);
        }
        return readConfiguredDomainType(getDomainTypeId(addonStats, slot), slot);
    }

    public static boolean shouldUseOpenBarrier(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return resolveCurrentDomainType(player).isOpenBarrier();
        }
        return OpenBarrierUnlockRules.shouldUseOpenBarrier(
            resolveCurrentDomainType(serverPlayer),
            OpenBarrierUnlockRules.hasAccess(hasSukunaEffect(serverPlayer), JjaAdvancementHelper.has(serverPlayer, MASTERY_OPEN_BARRIER_ID))
        );
    }

    public static AntiDomainPresentation resolveActivationPresentation(Entity entity) {
        return AntiDomainPresentation.fromOption(resolveCurrentAntiDomainOption(entity));
    }

    private static boolean hasOwnedSimpleDomain(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        MobEffectInstance effectInstance = livingEntity.getEffect(JujutsucraftModMobEffects.SIMPLE_DOMAIN.get());
        return effectInstance != null && effectInstance.getAmplifier() > 0;
    }

    private static boolean hasSukunaEffect(Entity entity) {
        return entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get());
    }

    private static int getAntiDomainOptionId(PlayerAddonStatsState addonStats, TechniqueSetupInputSlot slot) {
        return slot == TechniqueSetupInputSlot.CROUCH ? addonStats.getAntiDomainCrouchOptionId() : addonStats.getAntiDomainNormalOptionId();
    }

    private static boolean setAntiDomainOptionId(PlayerAddonStatsState addonStats, TechniqueSetupInputSlot slot, int optionId) {
        int current = getAntiDomainOptionId(addonStats, slot);
        if (current == optionId) {
            return false;
        }
        if (slot == TechniqueSetupInputSlot.CROUCH) {
            addonStats.setAntiDomainCrouchOptionId(optionId);
        } else {
            addonStats.setAntiDomainNormalOptionId(optionId);
        }
        return true;
    }

    private static int getDomainTypeId(PlayerAddonStatsState addonStats, TechniqueSetupInputSlot slot) {
        return slot == TechniqueSetupInputSlot.CROUCH ? addonStats.getDomainTypeCrouchOptionId() : addonStats.getDomainTypeNormalOptionId();
    }

    private static boolean setDomainTypeId(PlayerAddonStatsState addonStats, TechniqueSetupInputSlot slot, int optionId) {
        int current = getDomainTypeId(addonStats, slot);
        if (current == optionId) {
            return false;
        }
        if (slot == TechniqueSetupInputSlot.CROUCH) {
            addonStats.setDomainTypeCrouchOptionId(optionId);
        } else {
            addonStats.setDomainTypeNormalOptionId(optionId);
        }
        return true;
    }

    static AntiDomainTechniqueOption resolveInitializedAntiDomainOption(
        int currentId,
        int availableMask,
        int newlyAvailableMask,
        TechniqueSetupInputSlot slot,
        boolean preferHollowWickerBasketDefault
    ) {
        AntiDomainTechniqueOption current = AntiDomainTechniqueOption.fromId(currentId);
        if (current == AntiDomainTechniqueOption.NONE) {
            if (newlyAvailableMask == 0) {
                return AntiDomainTechniqueOption.NONE;
            }
            return defaultAntiDomainOption(slot, availableMask, preferHollowWickerBasketDefault);
        }
        return AntiDomainTechniqueOption.normalize(currentId, availableMask);
    }

    static AntiDomainTechniqueOption defaultAntiDomainOption(
        TechniqueSetupInputSlot slot,
        int availableMask,
        boolean preferHollowWickerBasketDefault
    ) {
        if (availableMask == 0) {
            return AntiDomainTechniqueOption.NONE;
        }
        AntiDomainTechniqueOption normalDefault = preferHollowWickerBasketDefault
            ? AntiDomainTechniqueOption.HOLLOW_WICKER_BASKET
            : AntiDomainTechniqueOption.SIMPLE_DOMAIN;
        if (!normalDefault.isAvailableIn(availableMask)) {
            normalDefault = AntiDomainTechniqueOption.firstAvailable(availableMask);
        }
        if (
            slot == TechniqueSetupInputSlot.CROUCH
            && AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION.isAvailableIn(availableMask)
        ) {
            return AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION;
        }
        return normalDefault;
    }

    static DomainTypeOption resolveInitializedDomainTypeOption(
        int currentId,
        int availableMask,
        int newlyAvailableMask,
        TechniqueSetupInputSlot slot
    ) {
        if (currentId == DomainTypeOption.NONE.id()) {
            if (newlyAvailableMask == 0 || availableMask == 0) {
                return DomainTypeOption.NONE;
            }
            return defaultDomainTypeOption(slot);
        }
        return readConfiguredDomainType(currentId, slot);
    }

    static DomainTypeOption defaultDomainTypeOption(TechniqueSetupInputSlot slot) {
        return slot == TechniqueSetupInputSlot.CROUCH ? DomainTypeOption.BARRIER : DomainTypeOption.OPEN_BARRIER;
    }

    static DomainTypeOption readConfiguredDomainType(int optionId, TechniqueSetupInputSlot slot) {
        if (optionId == DomainTypeOption.NONE.id()) {
            return DomainTypeOption.NONE;
        }
        if (optionId == DomainTypeOption.BARRIER.id() || optionId == DomainTypeOption.OPEN_BARRIER.id()) {
            return DomainTypeOption.fromId(optionId);
        }
        return defaultDomainTypeOption(slot);
    }

    static int newlyAvailableMask(int availableMask, int seenAvailableMask) {
        return availableMask & ~seenAvailableMask;
    }
}
