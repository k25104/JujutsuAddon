package com.arf8vhg7.jja.feature.equipment.curios;

import com.arf8vhg7.jja.compat.curios.JjaCuriosCompat;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class CuriosEquipmentMutationService {
    private static final String EQUIP_REPLACE_PREFIX = "item replace entity @s armor.";
    private static final String CLEAR_PREFIX = "clear @s ";

    private CuriosEquipmentMutationService() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromEquipmentSlot(equipmentSlot);
        if (logicalSlot == null) {
            return original;
        }
        return findManagedCuriosStack(livingEntity, logicalSlot).orElse(original);
    }

    public static boolean willHandlePlayerArmorInventorySet(Entity entity, int armorIndex, ItemStack stack) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromArmorInventoryIndex(armorIndex);
        if (logicalSlot == null || !(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        if (stack.isEmpty()) {
            return canHandleManagedClear(livingEntity, logicalSlot);
        }
        return classifyManagedEquip(livingEntity, logicalSlot, stack) != ManagedEquipState.NOT_HANDLED;
    }

    public static boolean handlePlayerArmorInventorySet(Entity entity, int armorIndex, ItemStack stack) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromArmorInventoryIndex(armorIndex);
        if (logicalSlot == null || !(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        if (stack.isEmpty()) {
            return tryClearManagedLogicalSlot(livingEntity, logicalSlot);
        }
        return tryEquipManaged(livingEntity, logicalSlot, stack);
    }

    public static boolean willHandleLivingArmorSet(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack stack) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromEquipmentSlot(equipmentSlot);
        if (logicalSlot == null) {
            return false;
        }
        if (stack.isEmpty()) {
            return canHandleManagedClear(livingEntity, logicalSlot);
        }
        return classifyManagedEquip(livingEntity, logicalSlot, stack) != ManagedEquipState.NOT_HANDLED;
    }

    public static boolean handleLivingArmorSet(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack stack) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromEquipmentSlot(equipmentSlot);
        if (logicalSlot == null) {
            return false;
        }
        if (stack.isEmpty()) {
            return tryClearManagedLogicalSlot(livingEntity, logicalSlot);
        }
        return tryEquipManaged(livingEntity, logicalSlot, stack);
    }

    public static boolean willHandleEquipCommand(Entity entity, String command) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        ManagedEquipCommand managedEquipCommand = parseEquipCommand(command);
        if (managedEquipCommand == null) {
            return false;
        }
        if (ForgeRegistries.ITEMS.getKey(Items.AIR).equals(managedEquipCommand.itemId())) {
            return canHandleManagedClear(livingEntity, managedEquipCommand.logicalSlot());
        }
        return classifyManagedEquip(
            livingEntity,
            managedEquipCommand.logicalSlot(),
            resolveManagedCommandStack(managedEquipCommand.itemId())
        ) != ManagedEquipState.NOT_HANDLED;
    }

    public static boolean tryHandleEquipCommand(Entity entity, String command) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        ManagedEquipCommand managedEquipCommand = parseEquipCommand(command);
        if (managedEquipCommand == null) {
            return false;
        }
        if (ForgeRegistries.ITEMS.getKey(Items.AIR).equals(managedEquipCommand.itemId())) {
            return tryClearManagedLogicalSlot(livingEntity, managedEquipCommand.logicalSlot());
        }
        return tryEquipManaged(
            livingEntity,
            managedEquipCommand.logicalSlot(),
            resolveManagedCommandStack(managedEquipCommand.itemId())
        );
    }

    public static void handlePostCommandCleanup(Entity entity, String command) {
        if (!(entity instanceof LivingEntity livingEntity) || !command.startsWith(CLEAR_PREFIX)) {
            return;
        }
        ResourceLocation itemId = ResourceLocation.tryParse(command.substring(CLEAR_PREFIX.length()));
        if (itemId == null) {
            return;
        }
        clearManagedItem(livingEntity, itemId);
    }

    public static boolean clearManagedItem(Entity entity, ResourceLocation itemId) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        CuriosLogicalSlot logicalSlot = CuriosManagedItemRegistry.resolveLogicalSlot(itemId);
        if (logicalSlot == null) {
            return false;
        }
        return clearLogicalSlot(livingEntity, logicalSlot, stack -> CuriosManagedItemRegistry.hasItemId(stack, itemId));
    }

    public static int clearMatchingManagedItems(LivingEntity livingEntity, Predicate<ItemStack> predicate, int maxCount) {
        if (maxCount <= 0) {
            return 0;
        }

        int removed = 0;
        for (CuriosLogicalSlot logicalSlot : CuriosLogicalSlot.values()) {
            if (removed >= maxCount) {
                break;
            }
            removed += clearMatchingItemsFromLogicalSlot(livingEntity, logicalSlot, predicate, maxCount - removed);
        }
        return removed;
    }

    public static boolean clearManagedCopies(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        boolean removed = JjaCuriosCompat.clearManagedCopies(livingEntity, CuriosManagedItemRegistry::isManagedItem);
        for (CuriosLogicalSlot logicalSlot : CuriosLogicalSlot.values()) {
            removed |= clearVanillaLogicalSlot(livingEntity, logicalSlot, stack -> CuriosManagedItemRegistry.isManagedForLogicalSlot(stack, logicalSlot));
        }
        return removed;
    }

    public static boolean isManagedItem(ItemStack stack) {
        return CuriosManagedItemRegistry.isManagedItem(stack);
    }

    @Nullable
    public static CuriosLogicalSlot resolveManagedLogicalSlot(ItemStack stack) {
        return CuriosManagedItemRegistry.resolveLogicalSlot(stack);
    }

    public static void forEachManagedItem(Consumer<Item> consumer) {
        CuriosManagedItemRegistry.forEachManagedItem(consumer);
    }

    public static void visitManagedCuriosStacks(LivingEntity livingEntity, Consumer<ItemStack> consumer) {
        for (CuriosLogicalSlot logicalSlot : CuriosLogicalSlot.values()) {
            JjaCuriosCompat.visitStacks(
                livingEntity,
                logicalSlot.curiosIdentifier(),
                stack -> {
                    if (CuriosManagedItemRegistry.isManagedForLogicalSlot(stack, logicalSlot)) {
                        consumer.accept(stack);
                    }
                }
            );
        }
    }

    public static boolean isManagedForEquipmentSlot(ItemStack stack, EquipmentSlot equipmentSlot) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromEquipmentSlot(equipmentSlot);
        return logicalSlot != null && isManagedForLogicalSlot(stack, logicalSlot);
    }

    public static boolean isManagedForLogicalSlot(ItemStack stack, CuriosLogicalSlot logicalSlot) {
        return CuriosManagedItemRegistry.isManagedForLogicalSlot(stack, logicalSlot);
    }

    private static boolean tryEquipManaged(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot, ItemStack stack) {
        return switch (classifyManagedEquip(livingEntity, logicalSlot, stack)) {
            case NOT_HANDLED -> false;
            case ALREADY_EQUIPPED -> true;
            case CAN_EQUIP -> {
                clearCuriosLogicalSlot(
                    livingEntity,
                    logicalSlot,
                    existing -> CuriosManagedItemRegistry.isManagedForLogicalSlot(existing, logicalSlot)
                );
                yield JjaCuriosCompat.equipManaged(
                    livingEntity,
                    logicalSlot.curiosIdentifier(),
                    stack,
                    existing -> existing.isEmpty() || CuriosManagedItemRegistry.isManagedForLogicalSlot(existing, logicalSlot)
                );
            }
        };
    }

    private static boolean tryClearManagedLogicalSlot(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot) {
        return clearLogicalSlot(
            livingEntity,
            logicalSlot,
            existing -> CuriosManagedItemRegistry.isManagedForLogicalSlot(existing, logicalSlot)
        );
    }

    private static boolean isManagedAlreadyEquipped(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot, ItemStack target) {
        Optional<ItemStack> curiosStack = findManagedCuriosStack(livingEntity, logicalSlot, stack -> isSameManagedItem(stack, target));
        if (curiosStack.isPresent()) {
            return true;
        }
        return isSameManagedItem(livingEntity.getItemBySlot(logicalSlot.equipmentSlot()), target);
    }

    private static boolean isSameManagedItem(ItemStack left, ItemStack right) {
        return !left.isEmpty() && !right.isEmpty() && left.getItem() == right.getItem();
    }

    private static ManagedEquipState classifyManagedEquip(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot, ItemStack stack) {
        if (!isManagedForLogicalSlot(stack, logicalSlot)) {
            return ManagedEquipState.NOT_HANDLED;
        }
        if (isManagedAlreadyEquipped(livingEntity, logicalSlot, stack)) {
            return ManagedEquipState.ALREADY_EQUIPPED;
        }
        return hasUsableManagedSlot(livingEntity, logicalSlot) ? ManagedEquipState.CAN_EQUIP : ManagedEquipState.NOT_HANDLED;
    }

    private static boolean canHandleManagedClear(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot) {
        if (findManagedCuriosStack(livingEntity, logicalSlot).isPresent()) {
            return true;
        }
        return CuriosManagedItemRegistry.isManagedForLogicalSlot(livingEntity.getItemBySlot(logicalSlot.equipmentSlot()), logicalSlot);
    }

    private static boolean hasUsableManagedSlot(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot) {
        return JjaCuriosCompat.hasUsableSlot(
            livingEntity,
            logicalSlot.curiosIdentifier(),
            existing -> existing.isEmpty() || CuriosManagedItemRegistry.isManagedForLogicalSlot(existing, logicalSlot)
        );
    }

    private static Optional<ItemStack> findManagedCuriosStack(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot) {
        return findManagedCuriosStack(
            livingEntity,
            logicalSlot,
            stack -> CuriosManagedItemRegistry.isManagedForLogicalSlot(stack, logicalSlot)
        );
    }

    private static Optional<ItemStack> findManagedCuriosStack(
        LivingEntity livingEntity,
        CuriosLogicalSlot logicalSlot,
        Predicate<ItemStack> predicate
    ) {
        return JjaCuriosCompat.findManagedStack(livingEntity, logicalSlot.curiosIdentifier(), predicate);
    }

    private static ItemStack resolveManagedCommandStack(ResourceLocation itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Nullable
    private static ManagedEquipCommand parseEquipCommand(String command) {
        if (!command.startsWith(EQUIP_REPLACE_PREFIX)) {
            return null;
        }
        String[] parts = command.split(" ");
        if (parts.length != 7 || !"with".equals(parts[5])) {
            return null;
        }
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromArmorCommandToken(parts[4]);
        ResourceLocation itemId = ResourceLocation.tryParse(parts[6]);
        if (logicalSlot == null || itemId == null) {
            return null;
        }
        return new ManagedEquipCommand(logicalSlot, itemId);
    }

    private static int clearMatchingItemsFromLogicalSlot(
        LivingEntity livingEntity,
        CuriosLogicalSlot logicalSlot,
        Predicate<ItemStack> predicate,
        int maxCount
    ) {
        return JjaCuriosCompat.clearMatchingItems(
            livingEntity,
            logicalSlot.curiosIdentifier(),
            stack -> CuriosManagedItemRegistry.isManagedForLogicalSlot(stack, logicalSlot) && predicate.test(stack),
            maxCount
        );
    }

    private static boolean clearLogicalSlot(
        LivingEntity livingEntity,
        CuriosLogicalSlot logicalSlot,
        Predicate<ItemStack> predicate
    ) {
        boolean removed = clearCuriosLogicalSlot(livingEntity, logicalSlot, predicate);
        return clearVanillaLogicalSlot(livingEntity, logicalSlot, predicate) || removed;
    }

    private static boolean clearCuriosLogicalSlot(
        LivingEntity livingEntity,
        CuriosLogicalSlot logicalSlot,
        Predicate<ItemStack> predicate
    ) {
        return JjaCuriosCompat.removeManagedFromLogicalSlot(livingEntity, logicalSlot.curiosIdentifier(), predicate);
    }

    private static boolean clearVanillaLogicalSlot(
        LivingEntity livingEntity,
        CuriosLogicalSlot logicalSlot,
        Predicate<ItemStack> predicate
    ) {
        ItemStack equipped = livingEntity.getItemBySlot(logicalSlot.equipmentSlot());
        if (!predicate.test(equipped)) {
            return false;
        }
        livingEntity.setItemSlot(logicalSlot.equipmentSlot(), ItemStack.EMPTY);
        return true;
    }

    private enum ManagedEquipState {
        NOT_HANDLED,
        ALREADY_EQUIPPED,
        CAN_EQUIP
    }

    private record ManagedEquipCommand(CuriosLogicalSlot logicalSlot, ResourceLocation itemId) {
    }
}
