package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.AntiDomainProgressionConfig;
import com.arf8vhg7.jja.feature.jja.domain.sd.HollowWickerBasketProgression;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import com.arf8vhg7.jja.feature.combat.damage.CombatCooldownStrengthScaling;
import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueNameKeyResolver;
import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuCopiedTechniqueRules;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementService;
import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;

public final class StartCursedTechniqueProcedureHook {
    private StartCursedTechniqueProcedureHook() {
    }

    public static String jjaGetTechniqueKeyOrString(Component component) {
        return JjaTechniqueNameKeyResolver.jjaGetTechniqueKeyOrString(component);
    }

    public static double scaleCombatCooldownAttackSpeedPenalty(Entity entity, double originalPenalty) {
        return CombatCooldownStrengthScaling.scaleAttackSpeedPenalty(entity, originalPenalty);
    }

    public static void onTechniqueStarted(Entity entity) {
        SummonEnhancementService.onTechniqueStarted(entity);
    }

    public static double getEffectiveCursePower(Entity entity, double originalCursePower) {
        return JjaCursePowerAccountingService.resolveEffectivePower(entity, originalCursePower);
    }

    public static double resolveRuntimeTechniqueCost(Entity entity, double displayedCost) {
        return resolveRuntimeTechniqueCost(displayedCost);
    }

    public static void refreshPlayerCursePowerFormer(Entity entity) {
        JjaCursePowerAccountingService.refreshPlayerCursePowerFormer(entity);
    }

    static double resolveRuntimeTechniqueCost(double displayedCost) {
        return displayedCost;
    }

    public static void queueTechniqueCost(JujutsucraftModVariables.PlayerVariables playerVariables, double resultingCursePower) {
        double effectiveCursePower = JjaCursePowerAccountingService.resolveEffectivePower(playerVariables, 0.0D);
        JjaCursePowerAccountingService.queueSpentPowerFromResult(playerVariables, effectiveCursePower, resultingCursePower);
    }

    public static int scaleCooldownTimeDuration(Entity entity, int originalDuration) {
        return ZoneChargeScalingService.scaleCooldownDuration(entity, originalDuration);
    }

    public static ResourceLocation remapTechniqueUsedAdvancement(ResourceLocation advancementId) {
        return AntiDomainProgressionConfig.isSdItemOnly()
            && HollowWickerBasketProgression.UPSTREAM_SIMPLE_DOMAIN_MASTERY_ID.equals(advancementId)
            ? HollowWickerBasketProgression.MASTERY_ID
            : advancementId;
    }

    public static Item resolveHeldLoudspeakerForTechniqueStart(Entity entity, Item originalItem) {
        return OkkotsuCopiedTechniqueRules.hasUnusedHeldLoudspeaker(entity) ? JujutsucraftModItems.LOUDSPEAKER.get() : originalItem;
    }

    public static boolean shouldWriteUsedFlagToOriginalStack(Entity entity) {
        return !OkkotsuCopiedTechniqueRules.markHeldLoudspeakerUsed(entity);
    }
}
