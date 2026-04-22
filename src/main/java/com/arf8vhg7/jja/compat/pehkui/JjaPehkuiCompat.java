package com.arf8vhg7.jja.compat.pehkui;

import com.arf8vhg7.jja.compat.JjaOptionalModHelper;
import com.arf8vhg7.jja.compat.JjaReflectiveCompatSupport;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public final class JjaPehkuiCompat {
    private static final float DEFAULT_BASE_SCALE = 1.0F;
    private static final float SCALE_EPSILON = 1.0E-4F;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PEHKUI_MODID = "pehkui";
    private static final String SCALE_TYPES_CLASS_NAME = "virtuoel.pehkui.api.ScaleTypes";
    private static final String SCALE_TYPE_CLASS_NAME = "virtuoel.pehkui.api.ScaleType";
    private static final String SCALE_DATA_CLASS_NAME = "virtuoel.pehkui.api.ScaleData";
    private static final String SCALE_UTILS_CLASS_NAME = "virtuoel.pehkui.util.ScaleUtils";

    private static final JjaReflectiveCompatSupport.InitState INIT_STATE = new JjaReflectiveCompatSupport.InitState();
    private static Field baseScaleTypeField;
    private static Field heightScaleTypeField;
    private static Method getScaleDataMethod;
    private static Method getBaseScaleMethod;
    private static Method setScaleMethod;
    private static Method getMotionScaleMethod;
    private static Method getBoundingBoxWidthScaleMethod;
    private static Method getBoundingBoxHeightScaleMethod;

    private JjaPehkuiCompat() {
    }

    public static boolean isPehkuiLoaded() {
        return JjaOptionalModHelper.isLoaded(PEHKUI_MODID);
    }

    public static float getBaseScale(@Nullable Entity entity) {
        Object scaleData = getScaleData(entity);
        if (scaleData == null) {
            return DEFAULT_BASE_SCALE;
        }
        try {
            return ((Float) getBaseScaleMethod.invoke(scaleData)).floatValue();
        } catch (ReflectiveOperationException exception) {
            logCompatError("read base scale", exception);
            return DEFAULT_BASE_SCALE;
        }
    }

    public static boolean isDefaultScale(float scale) {
        return Math.abs(scale - DEFAULT_BASE_SCALE) <= SCALE_EPSILON;
    }

    public static void setBaseScale(@Nullable Entity entity, float scale) {
        Object scaleData = getScaleData(entity);
        if (scaleData == null) {
            return;
        }
        float normalizedScale = normalizeScale(scale);
        try {
            float currentScale = ((Float) getBaseScaleMethod.invoke(scaleData)).floatValue();
            if (hasSameScale(currentScale, normalizedScale)) {
                return;
            }
            setScaleMethod.invoke(scaleData, normalizedScale);
        } catch (ReflectiveOperationException exception) {
            logCompatError("update base scale", exception);
        }
    }

    public static void resetBaseScale(@Nullable Entity entity) {
        setBaseScale(entity, DEFAULT_BASE_SCALE);
    }

    public static void setHeightScale(@Nullable Entity entity, float scale) {
        Object scaleData = getScaleData(entity, heightScaleTypeField);
        if (scaleData == null) {
            return;
        }
        float normalizedScale = normalizeScale(scale);
        try {
            float currentScale = ((Float) getBaseScaleMethod.invoke(scaleData)).floatValue();
            if (hasSameScale(currentScale, normalizedScale)) {
                return;
            }
            setScaleMethod.invoke(scaleData, normalizedScale);
        } catch (ReflectiveOperationException exception) {
            logCompatError("update height scale", exception);
        }
    }

    public static void resetHeightScale(@Nullable Entity entity) {
        setHeightScale(entity, DEFAULT_BASE_SCALE);
    }

    public static float getMotionScale(@Nullable Entity entity) {
        return getEntityScale(entity, getMotionScaleMethod, "read motion scale");
    }

    public static float getBoundingBoxWidthScale(@Nullable Entity entity) {
        return getEntityScale(entity, getBoundingBoxWidthScaleMethod, "read bounding box width scale");
    }

    public static float getBoundingBoxHeightScale(@Nullable Entity entity) {
        return getEntityScale(entity, getBoundingBoxHeightScaleMethod, "read bounding box height scale");
    }

    @Nullable
    private static Object getScaleData(@Nullable Entity entity) {
        return getScaleData(entity, baseScaleTypeField);
    }

    @Nullable
    private static Object getScaleData(@Nullable Entity entity, @Nullable Field scaleTypeField) {
        if (entity == null || !ensureInitialized()) {
            return null;
        }
        try {
            if (scaleTypeField == null) {
                return null;
            }
            Object scaleType = scaleTypeField.get(null);
            return getScaleDataMethod.invoke(scaleType, entity);
        } catch (ReflectiveOperationException exception) {
            logCompatError("resolve scale data", exception);
            return null;
        }
    }

    private static boolean ensureInitialized() {
        return JjaReflectiveCompatSupport.ensureInitialized(
            INIT_STATE,
            JjaPehkuiCompat.class,
            JjaPehkuiCompat::isPehkuiLoaded,
            () -> {
                Class<?> scaleTypesClass = Class.forName(SCALE_TYPES_CLASS_NAME);
                Class<?> scaleTypeClass = Class.forName(SCALE_TYPE_CLASS_NAME);
                Class<?> scaleDataClass = Class.forName(SCALE_DATA_CLASS_NAME);
                Class<?> scaleUtilsClass = Class.forName(SCALE_UTILS_CLASS_NAME);

                baseScaleTypeField = scaleTypesClass.getField("BASE");
                heightScaleTypeField = scaleTypesClass.getField("HEIGHT");
                getScaleDataMethod = scaleTypeClass.getMethod("getScaleData", Entity.class);
                getBaseScaleMethod = scaleDataClass.getMethod("getBaseScale");
                setScaleMethod = scaleDataClass.getMethod("setScale", float.class);
                getMotionScaleMethod = scaleUtilsClass.getMethod("getMotionScale", Entity.class);
                getBoundingBoxWidthScaleMethod = scaleUtilsClass.getMethod("getBoundingBoxWidthScale", Entity.class);
                getBoundingBoxHeightScaleMethod = scaleUtilsClass.getMethod("getBoundingBoxHeightScale", Entity.class);
            },
            exception -> {
                LOGGER.warn("Failed to initialize Pehkui compat reflection. Disabling Pehkui integration.", exception);
            }
        );
    }

    private static void logCompatError(String action, ReflectiveOperationException exception) {
        JjaReflectiveCompatSupport.logCompatError(LOGGER, "Pehkui", action, exception);
    }

    private static float getEntityScale(@Nullable Entity entity, @Nullable Method method, String action) {
        if (entity == null || method == null || !ensureInitialized()) {
            return DEFAULT_BASE_SCALE;
        }
        try {
            return ((Float) method.invoke(null, entity)).floatValue();
        } catch (ReflectiveOperationException exception) {
            logCompatError(action, exception);
            return DEFAULT_BASE_SCALE;
        }
    }

    private static float normalizeScale(float scale) {
        return Math.max(0.0F, scale);
    }

    private static boolean hasSameScale(float currentScale, float targetScale) {
        return Math.abs(currentScale - targetScale) <= SCALE_EPSILON;
    }
}
