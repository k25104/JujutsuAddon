package com.arf8vhg7.jja.compat.curios;

import com.arf8vhg7.jja.compat.JjaOptionalModHelper;
import com.arf8vhg7.jja.compat.JjaReflectiveCompatSupport;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public final class JjaCuriosItemCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CURIOS_MODID = "curios";
    private static final String CURIOS_API_CLASS_NAME = "top.theillusivec4.curios.api.CuriosApi";
    private static final String CURIOS_ITEM_INTERFACE_CLASS_NAME =
        "top.theillusivec4.curios.api.type.capability.ICurioItem";
    private static final String SLOT_CONTEXT_CLASS_NAME = "top.theillusivec4.curios.api.SlotContext";

    private static final JjaReflectiveCompatSupport.InitState INIT_STATE = new JjaReflectiveCompatSupport.InitState();
    private static Method registerCurioMethod;
    private static Method slotContextIdentifierMethod;
    private static Method slotContextEntityMethod;
    private static Class<?> curioItemInterface;

    private JjaCuriosItemCompat() {
    }

    public static boolean registerCurio(Item item, CurioItemBridge curioItemBridge) {
        if (!ensureInitialized()) {
            return false;
        }
        try {
            registerCurioMethod.invoke(null, item, createCurioItemProxy(curioItemBridge));
            return true;
        } catch (ReflectiveOperationException exception) {
            LOGGER.warn("Failed to register Curios item bridge for {}.", item, exception);
            return false;
        }
    }

    private static Object createCurioItemProxy(CurioItemBridge curioItemBridge) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return handleObjectMethod(proxy, method, args);
            }

            if ("getAttributeModifiers".equals(method.getName())
                && args != null
                && args.length == 3
                && args[1] instanceof UUID slotUuid
                && args[2] instanceof ItemStack stack) {
                return curioItemBridge.getAttributeModifiers(
                    resolveLivingEntity(args[0]),
                    resolveIdentifier(args[0]),
                    slotUuid,
                    stack
                );
            }

            if (method.isDefault()) {
                Object[] invocationArgs = args == null ? new Object[0] : args;
                return InvocationHandler.invokeDefault(proxy, method, invocationArgs);
            }

            return defaultValue(method.getReturnType());
        };
        return Proxy.newProxyInstance(curioItemInterface.getClassLoader(), new Class<?>[]{curioItemInterface}, handler);
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
            JjaCuriosItemCompat.class,
            () -> JjaOptionalModHelper.isLoaded(CURIOS_MODID),
            () -> {
                Class<?> curiosApiClass = Class.forName(CURIOS_API_CLASS_NAME);
                curioItemInterface = Class.forName(CURIOS_ITEM_INTERFACE_CLASS_NAME);
                Class<?> slotContextClass = Class.forName(SLOT_CONTEXT_CLASS_NAME);

                registerCurioMethod = curiosApiClass.getMethod("registerCurio", Item.class, curioItemInterface);
                slotContextIdentifierMethod = resolveSlotContextMethod(slotContextClass, "identifier", "getIdentifier");
                slotContextEntityMethod = resolveSlotContextMethod(slotContextClass, "entity", "getWearer");
            },
            exception -> {
                LOGGER.warn("Failed to initialize Curios item compat reflection.", exception);
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

    @Nullable
    private static Object handleObjectMethod(Object proxy, Method method, @Nullable Object[] args) {
        return switch (method.getName()) {
            case "toString" -> "JjaCuriosItemProxy";
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
        if (Multimap.class.isAssignableFrom(returnType)) {
            return ImmutableMultimap.of();
        }
        return null;
    }

    @FunctionalInterface
    public interface CurioItemBridge {
        Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            @Nullable LivingEntity livingEntity,
            @Nullable String identifier,
            UUID slotUuid,
            ItemStack stack
        );
    }
}
