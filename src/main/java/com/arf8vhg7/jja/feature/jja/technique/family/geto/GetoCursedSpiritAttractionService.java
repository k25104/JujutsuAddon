package com.arf8vhg7.jja.feature.jja.technique.family.geto;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.GetDistanceIteratorProcedure;
import net.mcreator.jujutsucraft.procedures.ReturnEntitySizeProcedure;
import net.mcreator.jujutsucraft.procedures.ResetCounterProcedure;
import net.mcreator.jujutsucraft.procedures.TechniqueBluePunchProcedure;
import net.mcreator.jujutsucraft.procedures.Test1Procedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;

public final class GetoCursedSpiritAttractionService {
    private static final ResourceLocation CURSED_SPIRIT_MANIPULATION_DIMENSION = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "cursed_spirit_manipulation_dimension"
    );
    static final int ATTACK_CHARGE_SLOWNESS_DURATION_TICKS = 5;
    static final int ATTACK_CHARGE_SLOWNESS_AMPLIFIER = 4;
    static final boolean ATTACK_CHARGE_SLOWNESS_AMBIENT = false;
    static final boolean ATTACK_CHARGE_SLOWNESS_VISIBLE = false;
    static final double BLUE_PUNCH_CAPTURE_DISTANCE = 2.5D;
    private static final ThreadLocal<BluePunchExecutionContext> ACTIVE_BLUE_PUNCH_CONTEXT = new ThreadLocal<>();

    private GetoCursedSpiritAttractionService() {
    }

    public static boolean tryHandle(LevelAccessor world, double x, double y, double z, @Nullable Entity entity) {
        if (entity == null || JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) != GetoTechniqueSelectionService.CURSED_SPIRIT_ATTRACTION_SKILL) {
            return false;
        }

        RuntimeAction action = resolveRuntimeAction(entity.getPersistentData().getBoolean("PRESS_Z"), entity instanceof Player);
        if (action == RuntimeAction.CLEAR) {
            return clearTechnique(entity);
        }

        executeBluePunch(world, (Player) entity);
        return true;
    }

    static RuntimeAction resolveRuntimeAction(boolean pressZ, boolean player) {
        return pressZ && player ? RuntimeAction.EXECUTE : RuntimeAction.CLEAR;
    }

    static boolean shouldNormalizeCompletedSkill(double skill) {
        int roundedSkill = (int) Math.round(skill);
        return roundedSkill == 1606 || roundedSkill == 101;
    }

    static BluePunchEffectAction resolveBluePunchEffectAction(boolean getoContext, boolean casterTarget, boolean slowness) {
        if (!getoContext || !slowness) {
            return BluePunchEffectAction.KEEP_ORIGINAL;
        }
        return casterTarget ? BluePunchEffectAction.REPLACE_WITH_ATTACK_CHARGE_SLOWNESS : BluePunchEffectAction.SUPPRESS;
    }

    static boolean resolveBluePunchAttackResult(boolean original, boolean getoContext, boolean recoverable, boolean captured) {
        if (!getoContext) {
            return original;
        }
        return recoverable && !captured;
    }

    static BluePunchTargetAction resolveBluePunchTargetAction(boolean recoverable, double distance, boolean captured) {
        return resolveBluePunchTargetAction(recoverable, distance, captured, BLUE_PUNCH_CAPTURE_DISTANCE);
    }

    static BluePunchTargetAction resolveBluePunchTargetAction(boolean recoverable, double distance, boolean captured, double captureDistance) {
        if (captured) {
            return BluePunchTargetAction.SKIP_VECTOR;
        }
        if (!recoverable) {
            return BluePunchTargetAction.SKIP_VECTOR;
        }
        if (distance >= captureDistance) {
            return BluePunchTargetAction.APPLY_VECTOR;
        }
        return BluePunchTargetAction.CAPTURE;
    }

    static double resolveBluePunchCaptureDistance(double entitySize) {
        return BLUE_PUNCH_CAPTURE_DISTANCE * entitySize;
    }

    static boolean isBluePunchSlowness(@Nullable MobEffectInstance effectInstance) {
        return effectInstance != null && effectInstance.getEffect() == MobEffects.MOVEMENT_SLOWDOWN;
    }

    static boolean shouldPlayBluePunchFrameSetSound(boolean getoContext) {
        return !getoContext;
    }

    static boolean shouldRunBluePunchGrab(boolean getoContext) {
        return !getoContext;
    }

    static boolean resolveBluePunchHoldLimitReached(boolean original, boolean getoContext) {
        return getoContext ? false : original;
    }

    public static boolean resolveBluePunchAttackResult(Entity caster, @Nullable Entity target) {
        boolean getoContext = isGetoBluePunchContext(caster);
        boolean recoverable = getoContext && caster instanceof Player player && isRecoverable(player, target);
        boolean captured = getoContext && isCaptured(target);
        return resolveBluePunchAttackResult(false, getoContext, recoverable, captured);
    }

    public static BluePunchTargetAction resolveBluePunchTargetAction(Entity caster, @Nullable Entity target) {
        if (!isGetoBluePunchContext(caster) || target == null) {
            return BluePunchTargetAction.APPLY_VECTOR;
        }
        boolean captured = isCaptured(target);
        boolean recoverable = caster instanceof Player player && isRecoverable(player, target);
        double distance = recoverable && !captured ? GetDistanceIteratorProcedure.execute(caster, target) : Double.POSITIVE_INFINITY;
        return resolveBluePunchTargetAction(
            recoverable,
            distance,
            captured,
            resolveBluePunchCaptureDistance(ReturnEntitySizeProcedure.execute(caster))
        );
    }

    public static void captureBluePunchTarget(Entity caster, @Nullable Entity target) {
        BluePunchExecutionContext context = ACTIVE_BLUE_PUNCH_CONTEXT.get();
        if (context == null || target == null || !context.markCaptured(target)) {
            return;
        }
        if (!caster.level().isClientSide()) {
            Test1Procedure.execute(caster.level(), target.getX(), target.getY(), target.getZ(), target, caster);
        }
    }

    public static BluePunchEffectAction resolveBluePunchEffectAction(Entity caster, @Nullable LivingEntity target, @Nullable MobEffectInstance effectInstance) {
        return resolveBluePunchEffectAction(isGetoBluePunchContext(caster), caster == target, isBluePunchSlowness(effectInstance));
    }

    public static MobEffectInstance createAttackChargeSlowness() {
        return new MobEffectInstance(
            Objects.requireNonNull(MobEffects.MOVEMENT_SLOWDOWN),
            ATTACK_CHARGE_SLOWNESS_DURATION_TICKS,
            ATTACK_CHARGE_SLOWNESS_AMPLIFIER,
            ATTACK_CHARGE_SLOWNESS_AMBIENT,
            ATTACK_CHARGE_SLOWNESS_VISIBLE
        );
    }

    public static boolean isGetoBluePunchContext(Entity caster) {
        return isBluePunchContext(caster);
    }

    public static boolean shouldPlayBluePunchFrameSetSound(Entity caster) {
        return shouldPlayBluePunchFrameSetSound(isGetoBluePunchContext(caster));
    }

    public static boolean shouldRunBluePunchGrab(Entity caster) {
        return shouldRunBluePunchGrab(isGetoBluePunchContext(caster));
    }

    public static boolean isBluePunchHoldLimitReached(Entity caster, boolean original) {
        return resolveBluePunchHoldLimitReached(original, isGetoBluePunchContext(caster));
    }

    private static void executeBluePunch(LevelAccessor world, Player player) {
        try (BluePunchContextScope ignored = enterBluePunchContext(player)) {
            TechniqueBluePunchProcedure.execute(world, player);
        }
        if (shouldNormalizeCompletedSkill(JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(player))) {
            clearTechnique(player);
        }
    }

    private static BluePunchContextScope enterBluePunchContext(Player player) {
        BluePunchExecutionContext previous = ACTIVE_BLUE_PUNCH_CONTEXT.get();
        ACTIVE_BLUE_PUNCH_CONTEXT.set(new BluePunchExecutionContext(player.getUUID(), new HashSet<>()));
        return new BluePunchContextScope(previous);
    }

    private static boolean isBluePunchContext(@Nullable Entity caster) {
        BluePunchExecutionContext context = ACTIVE_BLUE_PUNCH_CONTEXT.get();
        return context != null && context.matches(caster);
    }

    private static boolean isCaptured(@Nullable Entity target) {
        BluePunchExecutionContext context = ACTIVE_BLUE_PUNCH_CONTEXT.get();
        return context != null && context.isCaptured(target);
    }

    private static boolean clearTechnique(Entity entity) {
        ResetCounterProcedure.execute(entity);
        entity.getPersistentData().putBoolean("PRESS_Z", false);
        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(entity, 0.0D);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(Objects.requireNonNull(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get()));
        }
        return true;
    }

    private static boolean isRecoverable(Player player, @Nullable Entity target) {
        if (target == null) {
            return false;
        }
        return GetoCursedSpiritRecoveryRules.isRecoverable(buildRecoveryContext(player, target));
    }

    static GetoCursedSpiritRecoveryRules.RecoveryContext buildRecoveryContext(Player player, Entity target) {
        double playerFriendNum = JjaJujutsucraftDataAccess.jjaGetFriendNum(player);
        boolean friend = player.getStringUUID().equals(JjaJujutsucraftDataAccess.jjaGetOwnerUuid(target))
            && playerFriendNum != 0.0D
            && playerFriendNum == JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(target);
        MobEffect damageBoost = Objects.requireNonNull(MobEffects.DAMAGE_BOOST);
        MobEffectInstance playerStrengthEffect = player.hasEffect(damageBoost) ? player.getEffect(damageBoost) : null;
        int playerStrengthAmp = playerStrengthEffect == null ? 0 : playerStrengthEffect.getAmplifier();
        MobEffectInstance targetStrengthEffect = target instanceof LivingEntity livingEntity && livingEntity.hasEffect(damageBoost)
            ? livingEntity.getEffect(damageBoost)
            : null;
        int targetStrengthAmp = targetStrengthEffect == null ? 0 : targetStrengthEffect.getAmplifier();
        double targetCurrentHealth = target instanceof LivingEntity livingEntity ? livingEntity.getHealth() : 1.0D;
        double targetMaxHealth = target instanceof LivingEntity livingEntity ? livingEntity.getMaxHealth() : 1.0D;
        return new GetoCursedSpiritRecoveryRules.RecoveryContext(
            true,
            CURSED_SPIRIT_MANIPULATION_DIMENSION.equals(player.level().dimension().location()),
            hasGetoTechniqueInVariables(player),
            player.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.UNSTABLE.get())),
            target instanceof Player,
            target.isAlive(),
            target.getPersistentData().getBoolean("CursedSpirit"),
            target.getPersistentData().getDouble("select"),
            playerStrengthAmp + 1.0D,
            targetStrengthAmp,
            targetCurrentHealth,
            targetMaxHealth,
            player.getAbilities().instabuild,
            friend,
            JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(target)
        );
    }

    private static boolean hasGetoTechniqueInVariables(Player player) {
        var playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(player);
        return Math.round(playerVariables.PlayerCurseTechnique) == GetoTechniqueSelectionService.CURSE_TECHNIQUE_ID
            || Math.round(playerVariables.PlayerCurseTechnique2) == GetoTechniqueSelectionService.CURSE_TECHNIQUE_ID;
    }

    public enum RuntimeAction {
        CLEAR,
        EXECUTE
    }

    public enum BluePunchTargetAction {
        APPLY_VECTOR,
        CAPTURE,
        SKIP_VECTOR
    }

    public enum BluePunchEffectAction {
        KEEP_ORIGINAL,
        REPLACE_WITH_ATTACK_CHARGE_SLOWNESS,
        SUPPRESS
    }

    private record BluePunchExecutionContext(UUID casterUuid, Set<UUID> capturedTargets) {
        boolean matches(@Nullable Entity entity) {
            return entity != null && this.casterUuid.equals(entity.getUUID());
        }

        boolean isCaptured(@Nullable Entity entity) {
            return entity != null && this.capturedTargets.contains(entity.getUUID());
        }

        boolean markCaptured(@Nullable Entity entity) {
            return entity != null && this.capturedTargets.add(entity.getUUID());
        }
    }

    private static final class BluePunchContextScope implements AutoCloseable {
        private final BluePunchExecutionContext previous;
        private boolean closed;

        private BluePunchContextScope(BluePunchExecutionContext previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            if (this.previous == null) {
                ACTIVE_BLUE_PUNCH_CONTEXT.remove();
            } else {
                ACTIVE_BLUE_PUNCH_CONTEXT.set(this.previous);
            }
            this.closed = true;
        }
    }
}
