package com.arf8vhg7.jja.compat.curios;

import com.arf8vhg7.jja.compat.JjaOptionalModHelper;
import com.arf8vhg7.jja.compat.JjaReflectiveCompatSupport;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.slf4j.Logger;

public final class JjaCuriosCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CURIOS_MODID = "curios";
    private static final String CURIOS_API_CLASS_NAME = "top.theillusivec4.curios.api.CuriosApi";
    private static final String CURIOS_ITEM_HANDLER_CLASS_NAME = "top.theillusivec4.curios.api.type.capability.ICuriosItemHandler";
    private static final String CURIOS_STACKS_HANDLER_CLASS_NAME = "top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler";
    private static final String DYNAMIC_STACK_HANDLER_CLASS_NAME = "top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler";

    private static final JjaReflectiveCompatSupport.InitState INIT_STATE = new JjaReflectiveCompatSupport.InitState();
    private static Method getCuriosInventoryMethod;
    private static Method getStacksHandlerMethod;
    private static Method getCuriosMethod;
    private static Method setEquippedCurioMethod;
    private static Method getStacksMethod;
    private static Method getSlotsMethod;
    private static Method getStackInSlotMethod;

    private JjaCuriosCompat() {
    }

    public static boolean isCuriosLoaded() {
        return JjaOptionalModHelper.isLoaded(CURIOS_MODID);
    }

    public static boolean hasUsableSlot(@Nullable LivingEntity livingEntity, String identifier, Predicate<ItemStack> replaceable) {
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null) {
            return false;
        }
        return anyStack(handler, identifier, stack -> stack.isEmpty() || replaceable.test(stack));
    }

    public static Optional<ItemStack> findManagedStack(
        @Nullable LivingEntity livingEntity,
        String identifier,
        Predicate<ItemStack> predicate
    ) {
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null) {
            return Optional.empty();
        }
        return findStack(handler, identifier, stack -> !stack.isEmpty() && predicate.test(stack));
    }

    public static boolean equipManaged(
        @Nullable LivingEntity livingEntity,
        String identifier,
        ItemStack stack,
        Predicate<ItemStack> replaceable
    ) {
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null) {
            return false;
        }

        ItemStack normalized = stack.copy();
        normalized.setCount(1);
        int slotIndex = findSlotIndex(handler, identifier, existing -> existing.isEmpty() || replaceable.test(existing));
        return slotIndex >= 0 && setEquippedCurio(handler, identifier, slotIndex, normalized);
    }

    public static boolean removeManagedFromLogicalSlot(
        @Nullable LivingEntity livingEntity,
        String identifier,
        Predicate<ItemStack> predicate
    ) {
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null) {
            return false;
        }

        final boolean[] removed = {false};
        visitStacks(handler, identifier, (index, stack) -> {
            if (!stack.isEmpty() && predicate.test(stack) && setEquippedCurio(handler, identifier, index, ItemStack.EMPTY)) {
                removed[0] = true;
            }
            return true;
        });
        return removed[0];
    }

    public static int clearMatchingItems(
        @Nullable LivingEntity livingEntity,
        String identifier,
        Predicate<ItemStack> predicate,
        int maxCount
    ) {
        if (maxCount <= 0) {
            return 0;
        }
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null) {
            return 0;
        }

        final int[] removed = {0};
        visitStacks(handler, identifier, (index, stack) -> {
            if (removed[0] >= maxCount) {
                return false;
            }
            if (!stack.isEmpty() && predicate.test(stack) && setEquippedCurio(handler, identifier, index, ItemStack.EMPTY)) {
                removed[0] += Math.max(1, stack.getCount());
            }
            return removed[0] < maxCount;
        });
        return removed[0];
    }

    public static boolean clearManagedCopies(@Nullable LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null || !ensureInitialized()) {
            return false;
        }
        try {
            Object curios = getCuriosMethod.invoke(handler);
            if (!(curios instanceof Map<?, ?> curiosMap)) {
                return false;
            }

            final boolean[] removed = {false};
            visitIdentifiers(curiosMap, identifier -> {
                visitStacks(handler, identifier, (index, stack) -> {
                    if (!stack.isEmpty() && predicate.test(stack) && setEquippedCurio(handler, identifier, index, ItemStack.EMPTY)) {
                        removed[0] = true;
                    }
                    return true;
                });
            });
            return removed[0];
        } catch (ReflectiveOperationException exception) {
            logCompatError("clear Curios copies", exception);
            return false;
        }
    }

    public static void visitStacks(@Nullable LivingEntity livingEntity, String identifier, Consumer<ItemStack> consumer) {
        Object handler = getCuriosHandler(livingEntity);
        if (handler == null) {
            return;
        }

        visitStacks(handler, identifier, (index, stack) -> {
            if (!stack.isEmpty()) {
                consumer.accept(stack);
            }
            return true;
        });
    }

    @Nullable
    private static Object getCuriosHandler(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null || !ensureInitialized()) {
            return null;
        }
        try {
            Object optional = getCuriosInventoryMethod.invoke(null, livingEntity);
            if (optional instanceof LazyOptional<?> lazyOptional) {
                return lazyOptional.orElse(null);
            }
            return null;
        } catch (ReflectiveOperationException exception) {
            logCompatError("resolve Curios handler", exception);
            return null;
        }
    }

    private static int getSlotCount(Object handler, String identifier) {
        try {
            Object stacks = getStacks(handler, identifier);
            if (stacks == null) {
                return 0;
            }
            return (Integer) getSlotsMethod.invoke(stacks);
        } catch (ReflectiveOperationException exception) {
            logCompatError("read Curios slot count", exception);
            return 0;
        }
    }

    private static ItemStack getStack(Object handler, String identifier, int index) {
        try {
            Object stacks = getStacks(handler, identifier);
            if (stacks == null) {
                return ItemStack.EMPTY;
            }
            Object stack = getStackInSlotMethod.invoke(stacks, index);
            return stack instanceof ItemStack itemStack ? itemStack : ItemStack.EMPTY;
        } catch (ReflectiveOperationException exception) {
            logCompatError("read Curios stack", exception);
            return ItemStack.EMPTY;
        }
    }

    @Nullable
    private static Object getStacksHandler(Object handler, String identifier) throws ReflectiveOperationException {
        Object optional = getStacksHandlerMethod.invoke(handler, identifier);
        if (optional instanceof Optional<?> result) {
            return result.orElse(null);
        }
        return null;
    }

    @Nullable
    private static Object getStacks(Object handler, String identifier) throws ReflectiveOperationException {
        Object stacksHandler = getStacksHandler(handler, identifier);
        return stacksHandler == null ? null : getStacksMethod.invoke(stacksHandler);
    }

    private static boolean setEquippedCurio(Object handler, String identifier, int index, ItemStack stack) {
        try {
            setEquippedCurioMethod.invoke(handler, identifier, index, stack);
            return true;
        } catch (ReflectiveOperationException exception) {
            logCompatError("update Curios slot", exception);
            return false;
        }
    }

    private static boolean anyStack(Object handler, String identifier, Predicate<ItemStack> predicate) {
        return findSlotIndex(handler, identifier, predicate) >= 0;
    }

    private static Optional<ItemStack> findStack(Object handler, String identifier, Predicate<ItemStack> predicate) {
        final ItemStack[] matched = {ItemStack.EMPTY};
        visitStacks(handler, identifier, (index, stack) -> {
            if (predicate.test(stack)) {
                matched[0] = stack;
                return false;
            }
            return true;
        });
        return matched[0].isEmpty() ? Optional.empty() : Optional.of(matched[0]);
    }

    private static int findSlotIndex(Object handler, String identifier, Predicate<ItemStack> predicate) {
        final int[] slotIndex = {-1};
        visitStacks(handler, identifier, (index, stack) -> {
            if (predicate.test(stack)) {
                slotIndex[0] = index;
                return false;
            }
            return true;
        });
        return slotIndex[0];
    }

    private static void visitStacks(Object handler, String identifier, IndexedStackVisitor visitor) {
        for (int index = 0; index < getSlotCount(handler, identifier); index++) {
            if (!visitor.visit(index, getStack(handler, identifier, index))) {
                return;
            }
        }
    }

    private static void visitIdentifiers(Map<?, ?> curiosMap, Consumer<String> consumer) {
        for (Map.Entry<?, ?> entry : curiosMap.entrySet()) {
            if (entry.getKey() instanceof String identifier) {
                consumer.accept(identifier);
            }
        }
    }

    @FunctionalInterface
    private interface IndexedStackVisitor {
        boolean visit(int index, ItemStack stack);
    }

    private static boolean ensureInitialized() {
        return JjaReflectiveCompatSupport.ensureInitialized(
            INIT_STATE,
            JjaCuriosCompat.class,
            JjaCuriosCompat::isCuriosLoaded,
            () -> {
                Class<?> curiosApiClass = Class.forName(CURIOS_API_CLASS_NAME);
                Class<?> curiosItemHandlerClass = Class.forName(CURIOS_ITEM_HANDLER_CLASS_NAME);
                Class<?> curiosStacksHandlerClass = Class.forName(CURIOS_STACKS_HANDLER_CLASS_NAME);
                Class<?> dynamicStackHandlerClass = Class.forName(DYNAMIC_STACK_HANDLER_CLASS_NAME);

                getCuriosInventoryMethod = curiosApiClass.getMethod("getCuriosInventory", LivingEntity.class);
                getStacksHandlerMethod = curiosItemHandlerClass.getMethod("getStacksHandler", String.class);
                getCuriosMethod = curiosItemHandlerClass.getMethod("getCurios");
                setEquippedCurioMethod =
                    curiosItemHandlerClass.getMethod("setEquippedCurio", String.class, int.class, ItemStack.class);
                getStacksMethod = curiosStacksHandlerClass.getMethod("getStacks");
                getSlotsMethod = dynamicStackHandlerClass.getMethod("getSlots");
                getStackInSlotMethod = dynamicStackHandlerClass.getMethod("getStackInSlot", int.class);
            },
            exception -> {
                LOGGER.warn("Failed to initialize Curios compat reflection. Disabling Curios integration.", exception);
            }
        );
    }

    private static void logCompatError(String action, ReflectiveOperationException exception) {
        JjaReflectiveCompatSupport.logCompatError(LOGGER, "Curios", action, exception);
    }
}
