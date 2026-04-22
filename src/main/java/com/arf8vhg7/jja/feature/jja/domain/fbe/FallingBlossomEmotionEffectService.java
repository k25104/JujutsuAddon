package com.arf8vhg7.jja.feature.jja.domain.fbe;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public final class FallingBlossomEmotionEffectService {
    private static final ResourceLocation FBE_SOUND_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "frame_set");

    private FallingBlossomEmotionEffectService() {
    }

    public static void apply(LevelAccessor world, double x, double y, double z, Entity entity, double strength) {
        if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
            livingEntity.addEffect(
                new MobEffectInstance(JujutsucraftModMobEffects.FALLING_BLOSSOM_EMOTION.get(), Integer.MAX_VALUE, (int) strength, true, true)
            );
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 30, false, false));
        }

        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(
                Component.literal("§l" + Component.translatable("effect.jujutsucraft.falling_blossom_emotion").getString()),
                false
            );
        }

        if (!(world instanceof Level level)) {
            return;
        }
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(FBE_SOUND_ID);
        if (soundEvent == null) {
            return;
        }
        if (!level.isClientSide()) {
            level.playSound(null, BlockPos.containing(x, y, z), soundEvent, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return;
        }
        level.playLocalSound(x, y, z, soundEvent, SoundSource.NEUTRAL, 1.0F, 1.0F, false);
    }

    public static double computeStrength(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1.0D;
        }
        MobEffectInstance damageBoost = livingEntity.getEffect(MobEffects.DAMAGE_BOOST);
        return damageBoost != null ? damageBoost.getAmplifier() + 2.0D : 1.0D;
    }

    public static boolean canActivate(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(serverPlayer.level() instanceof ServerLevel)) {
            return false;
        }
        if (!FallingBlossomEmotionProgression.hasUnlocked(serverPlayer)) {
            return false;
        }
        return JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(player).PlayerCursePowerFormer > 50.0D;
    }
}
