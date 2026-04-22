package com.arf8vhg7.jja.feature.player.progression.fame;

import com.arf8vhg7.jja.feature.player.progression.fame.network.JjaFameGainClientMessage;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class FameGainMessageService {
    private static final String CURSED_SPIRIT_TAG = "CursedSpirit";
    private static final String CURSE_USER_TAG = "CurseUser";
    private static final String JUJUTSU_SORCERER_TAG = "JujutsuSorcerer";
    private static final String JJK_CHARA_TAG = "jjkChara";
    private static final String RESULT_KEY_REPUTATION = "jujutsu.message.kill3";
    private static final String RESULT_KEY_CURSE = "jujutsu.message.kill4";

    private FameGainMessageService() {
    }

    public static JjaFameGainClientMessage createClientMessage(Entity defeatedEntity, Entity receiver, double fame) {
        JjaFameGainClientMessage.TargetType targetType = resolveTargetType(defeatedEntity);
        if (targetType == null) {
            return null;
        }
        return createClientMessage(targetType, resolveResultKey(receiver), resolveStrengthAmplifier(defeatedEntity), fame);
    }

    static JjaFameGainClientMessage createClientMessage(
        JjaFameGainClientMessage.TargetType targetType,
        String resultKey,
        int strengthAmplifier,
        double fame
    ) {
        long fameAmount = resolveRoundedFame(fame);
        return new JjaFameGainClientMessage(targetType, resultKey, fameAmount, isMvp(strengthAmplifier, fameAmount));
    }

    static JjaFameGainClientMessage.TargetType resolveTargetType(Entity defeatedEntity) {
        if (defeatedEntity == null) {
            return null;
        }
        return resolveTargetType(
            defeatedEntity.getPersistentData().getBoolean(CURSED_SPIRIT_TAG),
            defeatedEntity.getPersistentData().getBoolean(CURSE_USER_TAG),
            defeatedEntity.getPersistentData().getBoolean(JUJUTSU_SORCERER_TAG),
            defeatedEntity.getPersistentData().getBoolean(JJK_CHARA_TAG)
        );
    }

    static JjaFameGainClientMessage.TargetType resolveTargetType(
        boolean cursedSpirit,
        boolean curseUser,
        boolean jujutsuSorcerer,
        boolean jjkChara
    ) {
        if (cursedSpirit) {
            return JjaFameGainClientMessage.TargetType.CURSED_SPIRIT;
        }
        if (curseUser) {
            return JjaFameGainClientMessage.TargetType.CURSE_USER;
        }
        if (jujutsuSorcerer) {
            return JjaFameGainClientMessage.TargetType.JUJUTSU_SORCERER;
        }
        if (jjkChara) {
            return JjaFameGainClientMessage.TargetType.JJK_CHARA;
        }
        return null;
    }

    static String resolveResultKey(Entity receiver) {
        return resolveResultKey(receiver != null && receiver.getPersistentData().getBoolean(CURSED_SPIRIT_TAG));
    }

    static String resolveResultKey(boolean cursedSpiritReceiver) {
        return cursedSpiritReceiver ? RESULT_KEY_CURSE : RESULT_KEY_REPUTATION;
    }

    static long resolveRoundedFame(double fame) {
        return Math.round(fame);
    }

    static boolean isMvp(int strengthAmplifier, long fameAmount) {
        return fameAmount == resolveMvpFameAmount(strengthAmplifier);
    }

    static long resolveMvpFameAmount(int strengthAmplifier) {
        long strength = Math.max(strengthAmplifier, 0);
        return (strength + 1L) * (strength + 1L) + 1L;
    }

    static int resolveStrengthAmplifier(Entity defeatedEntity) {
        if (!(defeatedEntity instanceof LivingEntity livingEntity)) {
            return 0;
        }
        MobEffectInstance damageBoost = livingEntity.getEffect(MobEffects.DAMAGE_BOOST);
        return damageBoost != null ? damageBoost.getAmplifier() : 0;
    }
}
