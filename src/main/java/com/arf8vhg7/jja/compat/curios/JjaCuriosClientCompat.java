package com.arf8vhg7.jja.compat.curios;

import com.arf8vhg7.jja.compat.JjaOptionalModHelper;
import com.arf8vhg7.jja.compat.JjaReflectiveCompatSupport;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public final class JjaCuriosClientCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CURIOS_MODID = "curios";
    private static final String CURIOS_RENDERER_REGISTRY_CLASS_NAME =
        "top.theillusivec4.curios.api.client.CuriosRendererRegistry";
    private static final String CURIOS_RENDERER_INTERFACE_CLASS_NAME =
        "top.theillusivec4.curios.api.client.ICurioRenderer";
    private static final String SLOT_CONTEXT_CLASS_NAME = "top.theillusivec4.curios.api.SlotContext";

    private static final JjaReflectiveCompatSupport.InitState INIT_STATE = new JjaReflectiveCompatSupport.InitState();
    private static Method registerMethod;
    private static Method slotContextIdentifierMethod;
    private static Method slotContextEntityMethod;
    private static Class<?> curioRendererInterface;

    private JjaCuriosClientCompat() {
    }

    public static boolean registerRenderer(Item item, CurioRendererBridge rendererBridge) {
        if (!ensureInitialized()) {
            return false;
        }
        try {
            registerMethod.invoke(null, item, (Supplier<Object>) () -> createRendererProxy(rendererBridge));
            return true;
        } catch (ReflectiveOperationException exception) {
            LOGGER.warn("Failed to register Curios renderer for {}.", item, exception);
            return false;
        }
    }

    private static Object createRendererProxy(CurioRendererBridge rendererBridge) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return handleObjectMethod(proxy, method, args);
            }
            if ("render".equals(method.getName()) && args != null && args.length == 12) {
                LivingEntity livingEntity = resolveLivingEntity(args[1]);
                String identifier = resolveIdentifier(args[1]);

                if (livingEntity != null && identifier != null) {
                    rendererBridge.render(
                        (ItemStack) args[0],
                        livingEntity,
                        identifier,
                        (PoseStack) args[2],
                        (RenderLayerParent<?, ?>) args[3],
                        (MultiBufferSource) args[4],
                        ((Integer) args[5]).intValue(),
                        ((Number) args[6]).floatValue(),
                        ((Number) args[7]).floatValue(),
                        ((Number) args[8]).floatValue(),
                        ((Number) args[9]).floatValue(),
                        ((Number) args[10]).floatValue(),
                        ((Number) args[11]).floatValue()
                    );
                }
                return null;
            }
            return defaultValue(method.getReturnType());
        };
        return Proxy.newProxyInstance(
            curioRendererInterface.getClassLoader(),
            new Class<?>[]{curioRendererInterface},
            handler
        );
    }

    @Nullable
    private static LivingEntity resolveLivingEntity(Object slotContext) throws ReflectiveOperationException {
        Object value = slotContextEntityMethod.invoke(slotContext);
        return value instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Nullable
    private static String resolveIdentifier(Object slotContext) throws ReflectiveOperationException {
        Object value = slotContextIdentifierMethod.invoke(slotContext);
        return value instanceof String identifier ? identifier : null;
    }

    private static boolean ensureInitialized() {
        return JjaReflectiveCompatSupport.ensureInitialized(
            INIT_STATE,
            JjaCuriosClientCompat.class,
            JjaCuriosClientCompat::isCuriosLoaded,
            () -> {
                Class<?> rendererRegistryClass = Class.forName(CURIOS_RENDERER_REGISTRY_CLASS_NAME);
                curioRendererInterface = Class.forName(CURIOS_RENDERER_INTERFACE_CLASS_NAME);
                Class<?> slotContextClass = Class.forName(SLOT_CONTEXT_CLASS_NAME);

                registerMethod = rendererRegistryClass.getMethod("register", Item.class, Supplier.class);
                slotContextIdentifierMethod = resolveSlotContextMethod(slotContextClass, "identifier", "getIdentifier");
                slotContextEntityMethod = resolveSlotContextMethod(slotContextClass, "entity", "getWearer");
            },
            exception -> {
                LOGGER.warn("Failed to initialize Curios client compat reflection.", exception);
            }
        );
    }

    private static Method resolveSlotContextMethod(Class<?> slotContextClass, String methodName, String fallbackMethodName)
    throws ReflectiveOperationException {
        try {
            return slotContextClass.getMethod(methodName);
        } catch (NoSuchMethodException exception) {
            LOGGER.debug(
                "Curios SlotContext method '{}' is unavailable; falling back to '{}'.",
                methodName,
                fallbackMethodName
            );
            return slotContextClass.getMethod(fallbackMethodName);
        }
    }

    private static boolean isCuriosLoaded() {
        return JjaOptionalModHelper.isLoaded(CURIOS_MODID);
    }

    @Nullable
    private static Object handleObjectMethod(Object proxy, Method method, @Nullable Object[] args) {
        return switch (method.getName()) {
            case "toString" -> "JjaCuriosRendererProxy";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == (args == null || args.length == 0 ? null : args[0]);
            default -> null;
        };
    }

    @Nullable
    private static Object defaultValue(Class<?> returnType) {
        if (returnType == Void.TYPE) {
            return null;
        }
        if (returnType == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (returnType == Character.TYPE) {
            return Character.valueOf('\0');
        }
        if (returnType == Byte.TYPE) {
            return Byte.valueOf((byte) 0);
        }
        if (returnType == Short.TYPE) {
            return Short.valueOf((short) 0);
        }
        if (returnType == Integer.TYPE) {
            return Integer.valueOf(0);
        }
        if (returnType == Long.TYPE) {
            return Long.valueOf(0L);
        }
        if (returnType == Float.TYPE) {
            return Float.valueOf(0.0F);
        }
        if (returnType == Double.TYPE) {
            return Double.valueOf(0.0D);
        }
        return null;
    }

    @FunctionalInterface
    public interface CurioRendererBridge {
        void render(
            ItemStack stack,
            LivingEntity livingEntity,
            String identifier,
            PoseStack poseStack,
            RenderLayerParent<?, ?> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
        );
    }
}
