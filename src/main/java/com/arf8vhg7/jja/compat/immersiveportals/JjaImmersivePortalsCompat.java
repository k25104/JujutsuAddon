package com.arf8vhg7.jja.compat.immersiveportals;

import com.arf8vhg7.jja.compat.JjaOptionalModHelper;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public final class JjaImmersivePortalsCompat {
    public static final String MODID = "immersive_portals";

    private static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    private static Api api;
    private static boolean initialized;

    private JjaImmersivePortalsCompat() {
    }

    public static boolean isLoaded() {
        return JjaOptionalModHelper.isLoaded(MODID);
    }

    public static boolean isAvailable() {
        return api() != null;
    }

    @Nullable
    public static Object createChunkLoader(ResourceKey<Level> dimension, int chunkX, int chunkZ, int radius) {
        Api resolvedApi = api();
        if (resolvedApi == null) {
            return null;
        }

        try {
            Object dimensionalChunkPos = resolvedApi.dimensionalChunkPosConstructor.newInstance(dimension, chunkX, chunkZ);
            return resolvedApi.chunkLoaderConstructor.newInstance(dimensionalChunkPos, radius);
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to create Immersive Portals chunk loader", exception);
            return null;
        }
    }

    public static boolean isChunkLoaderReady(@Nullable Object chunkLoader) {
        Api resolvedApi = api();
        if (resolvedApi == null || chunkLoader == null) {
            return false;
        }

        try {
            int loadedChunkCount = ((Number) resolvedApi.getLoadedChunkNumMethod.invoke(chunkLoader)).intValue();
            int totalChunkCount = ((Number) resolvedApi.getChunkNumMethod.invoke(chunkLoader)).intValue();
            return loadedChunkCount >= totalChunkCount;
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to query Immersive Portals chunk loader readiness", exception);
            return false;
        }
    }

    public static void addGlobalChunkLoader(@Nullable Object chunkLoader) {
        Api resolvedApi = api();
        if (resolvedApi == null || chunkLoader == null) {
            return;
        }

        try {
            resolvedApi.addGlobalChunkLoaderMethod.invoke(null, chunkLoader);
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to add Immersive Portals global chunk loader", exception);
        }
    }

    public static void removeGlobalChunkLoader(@Nullable Object chunkLoader) {
        Api resolvedApi = api();
        if (resolvedApi == null || chunkLoader == null) {
            return;
        }

        try {
            resolvedApi.removeGlobalChunkLoaderMethod.invoke(null, chunkLoader);
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to remove Immersive Portals global chunk loader", exception);
        }
    }

    public static boolean teleportEntity(Entity entity, ServerLevel targetLevel, Vec3 targetPos) {
        Api resolvedApi = api();
        if (resolvedApi == null) {
            return false;
        }

        try {
            resolvedApi.teleportEntityMethod.invoke(null, entity, targetLevel, targetPos);
            return true;
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to teleport entity with Immersive Portals API", exception);
            return false;
        }
    }

    @Nullable
    private static Api api() {
        if (!isLoaded()) {
            return null;
        }

        if (initialized) {
            return api;
        }

        initialized = true;
        try {
            api = new Api();
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to initialize Immersive Portals compat", exception);
            api = null;
        }

        return api;
    }

    private static final class Api {
        private final Constructor<?> dimensionalChunkPosConstructor;
        private final Constructor<?> chunkLoaderConstructor;
        private final Method getLoadedChunkNumMethod;
        private final Method getChunkNumMethod;
        private final Method addGlobalChunkLoaderMethod;
        private final Method removeGlobalChunkLoaderMethod;
        private final Method teleportEntityMethod;

        private Api() throws ReflectiveOperationException {
            Class<?> portalApiClass = Class.forName("qouteall.imm_ptl.core.api.PortalAPI");
            Class<?> dimensionalChunkPosClass = Class.forName("qouteall.imm_ptl.core.chunk_loading.DimensionalChunkPos");
            Class<?> chunkLoaderClass = Class.forName("qouteall.imm_ptl.core.chunk_loading.ChunkLoader");
            Class<?> chunkTrackingGraphClass = Class.forName("qouteall.imm_ptl.core.chunk_loading.NewChunkTrackingGraph");

            this.dimensionalChunkPosConstructor = dimensionalChunkPosClass.getConstructor(ResourceKey.class, int.class, int.class);
            this.chunkLoaderConstructor = chunkLoaderClass.getConstructor(dimensionalChunkPosClass, int.class);
            this.getLoadedChunkNumMethod = chunkLoaderClass.getMethod("getLoadedChunkNum");
            this.getChunkNumMethod = chunkLoaderClass.getMethod("getChunkNum");
            this.addGlobalChunkLoaderMethod = chunkTrackingGraphClass.getMethod("addGlobalAdditionalChunkLoader", chunkLoaderClass);
            this.removeGlobalChunkLoaderMethod = chunkTrackingGraphClass.getMethod("removeGlobalAdditionalChunkLoader", chunkLoaderClass);
            this.teleportEntityMethod = portalApiClass.getMethod("teleportEntity", Entity.class, ServerLevel.class, Vec3.class);
        }
    }
}
