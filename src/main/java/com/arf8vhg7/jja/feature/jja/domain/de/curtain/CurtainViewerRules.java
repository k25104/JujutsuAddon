package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.hook.jujutsucraft.PlayerPhysicalAbilityProcedureHook;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables.PlayerVariables;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

public final class CurtainViewerRules {
    private static final double TRANSPARENT_CURSE_POWER_FORMER_MAX = 5.0D;

    private CurtainViewerRules() {
    }

    public static CurtainShellVisionMode resolveShellVisionMode(double playerCursePowerFormer, boolean completePhysicalGifted) {
        return !completePhysicalGifted && playerCursePowerFormer > TRANSPARENT_CURSE_POWER_FORMER_MAX
            ? CurtainShellVisionMode.BLACK
            : CurtainShellVisionMode.TRANSPARENT;
    }

    public static CurtainShellVisionMode resolveShellVisionMode(@Nullable Entity viewer) {
        return resolveShellVisionMode(resolvePlayerCursePowerFormer(viewer), isCompletePhysicalGifted(viewer));
    }

    public static boolean shouldHideTargetFromViewer(
        boolean viewerOutside,
        boolean targetInside,
        boolean viewerCompletePhysicalGifted,
        boolean targetCompletePhysicalGifted
    ) {
        return viewerOutside && targetInside && !viewerCompletePhysicalGifted && !targetCompletePhysicalGifted;
    }

    public static boolean shouldHideTargetFromViewer(@Nullable Entity viewer, @Nullable Entity target, boolean viewerOutside, boolean targetInside) {
        return shouldHideTargetFromViewer(viewerOutside, targetInside, isCompletePhysicalGifted(viewer), isCompletePhysicalGifted(target));
    }

    public static double resolvePlayerCursePowerFormer(@Nullable Entity entity) {
        PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        return playerVariables == null ? 0.0D : playerVariables.PlayerCursePowerFormer;
    }

    public static boolean isCompletePhysicalGifted(@Nullable Entity entity) {
        if (!(entity instanceof net.minecraft.world.entity.LivingEntity livingEntity)) {
            return false;
        }

        MobEffect effect = Objects.requireNonNull(JujutsucraftModMobEffects.PHYSICAL_GIFTED_EFFECT.get());
        MobEffectInstance physicalGiftedEffect = livingEntity.getEffect(effect);
        return PlayerPhysicalAbilityProcedureHook.hasCompletePhysicalGifted(physicalGiftedEffect);
    }
}
