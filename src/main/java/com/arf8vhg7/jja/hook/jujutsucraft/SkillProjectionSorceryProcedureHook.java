package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.naoya.NaoyaProjectionSorceryService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.Objects;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class SkillProjectionSorceryProcedureHook {
    private static final int SKILL_FREEZE_DURATION_TICKS = 20;

    private SkillProjectionSorceryProcedureHook() {
    }

    public static boolean applyProjectionSorceryEffect(
        LevelAccessor world,
        Entity caster,
        LivingEntity target,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        if (NaoyaProjectionSorceryService.shouldSkipProjectionFreezeEffect(caster, target, effectInstance)) {
            return false;
        }

        MobEffectInstance adjustedEffectInstance = resolveSkillFreezeEffectInstance(caster, target, effectInstance);
        boolean applied = original.call(target, adjustedEffectInstance);
        NaoyaProjectionSorceryService.handleProjectionFreezeEffectApplied(world, caster, target, adjustedEffectInstance, applied);
        return applied;
    }

    public static boolean jjaShouldKeepProjectionFollowLocked(
        Entity entity,
        CompoundTag persistentData,
        String key,
        Operation<Boolean> original
    ) {
        boolean pressZ = original.call(persistentData, key);
        return NaoyaProjectionSorceryService.jjaShouldKeepProjectionFollowLocked(entity, pressZ);
    }

    private static MobEffectInstance resolveSkillFreezeEffectInstance(Entity caster, LivingEntity target, MobEffectInstance effectInstance) {
        if (target == caster
            || effectInstance.getEffect() != JujutsucraftModMobEffects.PROJECTION_SORCERY.get()
            || effectInstance.getDuration() == SKILL_FREEZE_DURATION_TICKS) {
            return effectInstance;
        }
        return new MobEffectInstance(
            Objects.requireNonNull(effectInstance.getEffect()),
            SKILL_FREEZE_DURATION_TICKS,
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            effectInstance.isVisible(),
            effectInstance.showIcon()
        );
    }
}
