package com.arf8vhg7.jja.feature.player.mobility.fly;

import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class FlyEffectGrantRules {
    public static final int CHESTPLATE_FLY_INCREMENT = 4;
    public static final int PASSIVE_FLY_INCREMENT = 10;
    public static final int MAX_FLY_DURATION = 60;
    public static final int PHYSICAL_FLY_TECHNIQUE_ID = 38;
    public static final int KAORI_TECHNIQUE_ID = 41;

    private static final ResourceLocation HITEN_ITEM_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "hiten");

    private FlyEffectGrantRules() {
    }

    public static boolean hasHitenInHands(@Nullable ResourceLocation mainHandItemId, @Nullable ResourceLocation offHandItemId) {
        return HITEN_ITEM_ID.equals(mainHandItemId) || HITEN_ITEM_ID.equals(offHandItemId);
    }

    public static boolean shouldCancelCharacterGate(boolean chestplateBlocked, boolean hitenHeld) {
        return chestplateBlocked && !hitenHeld;
    }

    public static boolean shouldSuppressChestplateRead(boolean chestplateBlocked, boolean hitenHeld) {
        return chestplateBlocked && hitenHeld;
    }

    public static boolean shouldApplyKaoriPassiveFlySupplement(double playerCt1, double playerCt2, boolean passiveActive) {
        return passiveActive
            && hasTechnique(playerCt1, playerCt2, KAORI_TECHNIQUE_ID)
            && !hasTechnique(playerCt1, playerCt2, PHYSICAL_FLY_TECHNIQUE_ID);
    }

    public static boolean shouldExtendGroundedFlyEffect(boolean onGround, boolean inWater, int cooldownBackStepAmplifier) {
        return (onGround || inWater) && cooldownBackStepAmplifier < 5;
    }

    public static int extendDuration(int currentDuration, int increment) {
        return Math.min(currentDuration + increment, MAX_FLY_DURATION);
    }

    public static boolean hasHitenInHands(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null) {
            return false;
        }
        return hasHitenInHands(resolveItemId(livingEntity.getMainHandItem()), resolveItemId(livingEntity.getOffhandItem()));
    }

    public static void applyHitenGroundedFlyEffect(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity) || !hasHitenInHands(livingEntity)) {
            return;
        }
        applyGroundedFlyEffect(livingEntity, CHESTPLATE_FLY_INCREMENT);
    }

    public static void applyGroundedFlyEffect(@Nullable LivingEntity livingEntity, int increment) {
        if (livingEntity == null || livingEntity.level().isClientSide()) {
            return;
        }
        if (!shouldExtendGroundedFlyEffect(livingEntity.onGround(), livingEntity.isInWater(), getCooldownBackStepAmplifier(livingEntity))) {
            return;
        }
        livingEntity.addEffect(new MobEffectInstance(
            JujutsucraftModMobEffects.FLY_EFFECT.get(),
            extendDuration(getCurrentFlyDuration(livingEntity), increment),
            0,
            true,
            true
        ));
    }

    private static boolean hasTechnique(double playerCt1, double playerCt2, int techniqueId) {
        return Math.round(playerCt1) == techniqueId || Math.round(playerCt2) == techniqueId;
    }

    private static int getCurrentFlyDuration(LivingEntity livingEntity) {
        MobEffectInstance flyEffect = livingEntity.getEffect(JujutsucraftModMobEffects.FLY_EFFECT.get());
        return flyEffect != null ? flyEffect.getDuration() : 0;
    }

    private static int getCooldownBackStepAmplifier(LivingEntity livingEntity) {
        MobEffectInstance cooldownBackStep = livingEntity.getEffect(JujutsucraftModMobEffects.COOLDOWN_TIME_BACK_STEP.get());
        return cooldownBackStep != null ? cooldownBackStep.getAmplifier() : 0;
    }

    @Nullable
    private static ResourceLocation resolveItemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() == JujutsucraftModItems.HITEN.get()) {
            return HITEN_ITEM_ID;
        }
        return ForgeRegistries.ITEMS.getKey(stack.getItem());
    }
}
