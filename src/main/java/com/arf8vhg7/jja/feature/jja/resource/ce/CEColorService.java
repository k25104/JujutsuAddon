package com.arf8vhg7.jja.feature.jja.resource.ce;

import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.GetEntityFromUUIDProcedure;
import net.mcreator.jujutsucraft.procedures.ReturnEnergyColorProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class CEColorService {
    private static final ThreadLocal<Integer> OVERRIDE_SUPPRESSION_DEPTH = ThreadLocal.withInitial(() -> 0);

    private CEColorService() {
    }

    public static @Nullable Integer getOverrideColor(@Nullable Entity entity) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (isOverrideSuppressed() || !(entity instanceof Player) || addonStats == null || !addonStats.hasCeColorOverride()) {
            return null;
        }
        if (!allowsOverride(entity)) {
            return null;
        }
        return addonStats.getCeColorOverride();
    }

    public static boolean allowsOverride(@Nullable Entity entity) {
        return Double.compare(resolveBaseColor(entity), 0.0D) > 0;
    }

    public static int randomColorId() {
        return CEColorName.randomColorId();
    }

    public static @Nullable Integer parseLiteralColorId(String literal) {
        CEColorName colorName = CEColorName.fromLiteral(literal);
        return colorName == null ? null : colorName.id();
    }

    public static @Nullable String resolveCurrentColorLiteral(@Nullable Entity entity) {
        CEColorName colorName = resolveCurrentColorName(entity);
        return colorName == null ? null : colorName.literal();
    }

    public static double resolveBaseColor(@Nullable Entity entity) {
        if (entity == null) {
            return 0.0D;
        }
        return withOverrideSuppressed(() -> ReturnEnergyColorProcedure.execute(entity));
    }

    public static double resolveCurrentColor(@Nullable Entity entity) {
        Integer overrideColor = getOverrideColor(entity);
        if (overrideColor != null) {
            return overrideColor.doubleValue();
        }
        return resolveBaseColor(entity);
    }

    public static String resolveSixEyesParticleCommand(@Nullable Entity entity, String originalCommand) {
        Integer overrideColor = getOverrideColor(entity);
        if (overrideColor == null) {
            return originalCommand;
        }

        CEColorName colorName = CEColorName.fromId(overrideColor);
        if (colorName == null) {
            return originalCommand;
        }

        int secondSpace = originalCommand.indexOf(' ', originalCommand.indexOf(' ') + 1);
        if (secondSpace < 0) {
            return originalCommand;
        }
        return "particle " + colorName.sixEyesParticleName() + originalCommand.substring(secondSpace);
    }

    static @Nullable CEColorName resolveCurrentColorName(@Nullable Entity entity) {
        return CEColorName.fromId(resolveCurrentColor(entity));
    }

    static @Nullable Entity resolveParticleOwner(@Nullable Entity sourceEntity, @Nullable Entity explicitOwner) {
        if (explicitOwner != null) {
            return explicitOwner;
        }
        Entity owner = resolveOwnerFromPersistentData(sourceEntity);
        return owner != null ? owner : sourceEntity;
    }

    private static @Nullable Entity resolveOwnerFromPersistentData(@Nullable Entity sourceEntity) {
        if (sourceEntity == null || !sourceEntity.getPersistentData().contains("OWNER_UUID")) {
            return null;
        }
        String ownerUuid = sourceEntity.getPersistentData().getString("OWNER_UUID");
        if (ownerUuid.isBlank()) {
            return null;
        }
        return GetEntityFromUUIDProcedure.execute(sourceEntity.level(), ownerUuid);
    }

    private static boolean isOverrideSuppressed() {
        return OVERRIDE_SUPPRESSION_DEPTH.get() > 0;
    }

    private static <T> T withOverrideSuppressed(Supplier<T> supplier) {
        int depth = OVERRIDE_SUPPRESSION_DEPTH.get();
        OVERRIDE_SUPPRESSION_DEPTH.set(depth + 1);
        try {
            return supplier.get();
        } finally {
            if (depth == 0) {
                OVERRIDE_SUPPRESSION_DEPTH.remove();
            } else {
                OVERRIDE_SUPPRESSION_DEPTH.set(depth);
            }
        }
    }
}
