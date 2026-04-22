package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class DomainExpansionConfiguredRadiusSync {
    private static boolean dirty = true;

    private DomainExpansionConfiguredRadiusSync() {
    }

    public static void markDirty() {
        dirty = true;
    }

    public static double getConfiguredRadius() {
        return JjaCommonConfig.DOMAIN_EXPANSION_RADIUS.get().doubleValue();
    }

    public static void refresh(@Nullable MinecraftServer server) {
        if (server == null) {
            return;
        }

        double configuredRadius = getConfiguredRadius();
        boolean needsSync = dirty;
        for (ServerLevel level : server.getAllLevels()) {
            JujutsucraftModVariables.MapVariables mapVariables = JujutsucraftModVariables.MapVariables.get(level);
            if (Double.compare(mapVariables.DomainExpansionRadius, configuredRadius) != 0) {
                needsSync = true;
                break;
            }
        }

        if (!needsSync) {
            return;
        }

        for (ServerLevel level : server.getAllLevels()) {
            JujutsucraftModVariables.MapVariables mapVariables = JujutsucraftModVariables.MapVariables.get(level);
            mapVariables.DomainExpansionRadius = configuredRadius;
            mapVariables.syncData(level);
        }

        dirty = false;
    }
}
