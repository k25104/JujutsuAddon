package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import java.util.UUID;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.LogicAttackProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RctOutputService {
    private static final int OUTPUT_EFFECT_DURATION = 2;
    private static final int OUTPUT_EFFECT_AMPLIFIER = 0;

    private RctOutputService() {
    }

    public static int applyPlayerOutput(LevelAccessor world, LivingEntity caster) {
        if (!JjaCommonConfig.RCT_OUTPUT_ENABLED.get()) {
            return 0;
        }
        if (!(caster instanceof net.minecraft.server.level.ServerPlayer player) || caster.level().isClientSide()) {
            return 0;
        }
        if (!RctContextService.canApplyOutput(player)) {
            return 0;
        }
        double distance = caster.getBbWidth();
        Vec3 center = new Vec3(
            caster.getX() + caster.getLookAngle().x * distance,
            caster.getY() + caster.getBbHeight() * 0.9 + caster.getLookAngle().y * distance,
            caster.getZ() + caster.getLookAngle().z * distance
        );
        var candidates = world.getEntitiesOfClass(
            LivingEntity.class,
            new AABB(center, center).inflate(distance * 3.0 / 2.0),
            target -> target != caster && target.isAlive()
        );
        int successCount = 0;
        for (LivingEntity livingTarget : candidates) {
            if (!shouldTarget(world, caster, livingTarget, player)) {
                continue;
            }
            if (!RctContextService.canReceiveRctOutput(livingTarget)) {
                continue;
            }
            applyOutputEffect(livingTarget);
            successCount++;
        }
        return successCount;
    }

    private static boolean shouldTarget(LevelAccessor world, LivingEntity caster, LivingEntity target, ServerPlayer player) {
        boolean targetIsCursedSpirit = RctMath.isCursedSpirit(target);
        if (target instanceof Player) {
            PlayerRctState rctState = PlayerStateAccess.rct(player);
            return shouldTargetPlayer(rctState, target.getUUID());
        }
        boolean hostile = LogicAttackProcedure.execute(world, target, caster);
        return targetIsCursedSpirit ? hostile : !hostile;
    }

    static boolean shouldTargetPlayer(PlayerRctState rctState, UUID targetId) {
        return rctState != null && targetId != null && rctState.hasAttackTarget(targetId);
    }

    private static void applyOutputEffect(LivingEntity target) {
        target.addEffect(
            new MobEffectInstance(
                (MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get(),
                OUTPUT_EFFECT_DURATION,
                OUTPUT_EFFECT_AMPLIFIER
            )
        );
    }
}
