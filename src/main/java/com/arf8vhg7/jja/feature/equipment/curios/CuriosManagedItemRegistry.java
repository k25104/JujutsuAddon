package com.arf8vhg7.jja.feature.equipment.curios;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class CuriosManagedItemRegistry {
    private static final Map<ResourceLocation, CuriosLogicalSlot> MANAGED_ITEM_SLOTS = Map.ofEntries(
        Map.entry(id("mahoraga_wheel_helmet"), CuriosLogicalSlot.HEAD),
        Map.entry(id("mahoraga_body_helmet"), CuriosLogicalSlot.HEAD),
        Map.entry(id("insect_armor_helmet"), CuriosLogicalSlot.HEAD),
        Map.entry(id("armor_instant_spirit_bodyof_distorted_killing_helmet"), CuriosLogicalSlot.HEAD),
        Map.entry(id("mythical_beast_amber_helmet"), CuriosLogicalSlot.HEAD),
        Map.entry(id("costume_takaba_helmet"), CuriosLogicalSlot.HEAD),
        Map.entry(id("clothes_angel_chestplate"), CuriosLogicalSlot.BODY),
        Map.entry(id("clothes_dagon_chestplate"), CuriosLogicalSlot.BODY),
        Map.entry(id("wing_king_chestplate"), CuriosLogicalSlot.BODY),
        Map.entry(id("insect_armor_chestplate"), CuriosLogicalSlot.BODY),
        Map.entry(id("armor_instant_spirit_bodyof_distorted_killing_chestplate"), CuriosLogicalSlot.BODY),
        Map.entry(id("insect_armor_leggings"), CuriosLogicalSlot.LEGS),
        Map.entry(id("armor_instant_spirit_bodyof_distorted_killing_leggings"), CuriosLogicalSlot.LEGS)
    );
    private static final Set<ResourceLocation> ARMOR_OVERRIDE_ITEM_IDS = Set.of(
        id("insect_armor_helmet"),
        id("insect_armor_chestplate"),
        id("insect_armor_leggings"),
        id("armor_instant_spirit_bodyof_distorted_killing_helmet"),
        id("armor_instant_spirit_bodyof_distorted_killing_chestplate"),
        id("armor_instant_spirit_bodyof_distorted_killing_leggings")
    );

    @Nullable
    private static volatile List<Item> resolvedManagedItems;
    @Nullable
    private static volatile List<Item> resolvedArmorOverrideItems;

    private CuriosManagedItemRegistry() {
    }

    public static boolean isManagedItem(ItemStack stack) {
        return resolveLogicalSlot(stack) != null;
    }

    public static boolean isManagedForLogicalSlot(ItemStack stack, CuriosLogicalSlot logicalSlot) {
        return logicalSlot == resolveLogicalSlot(stack);
    }

    public static boolean isArmorOverrideItem(ItemStack stack) {
        ResourceLocation itemId = itemId(stack);
        return itemId != null && ARMOR_OVERRIDE_ITEM_IDS.contains(itemId);
    }

    public static boolean isArmorOverrideForLogicalSlot(ItemStack stack, CuriosLogicalSlot logicalSlot) {
        return logicalSlot == resolveArmorOverrideLogicalSlot(stack);
    }

    @Nullable
    public static CuriosLogicalSlot resolveLogicalSlot(ItemStack stack) {
        ResourceLocation itemId = itemId(stack);
        return itemId == null ? null : resolveLogicalSlot(itemId);
    }

    @Nullable
    public static CuriosLogicalSlot resolveLogicalSlot(@Nullable ResourceLocation itemId) {
        return itemId == null ? null : MANAGED_ITEM_SLOTS.get(itemId);
    }

    public static boolean hasItemId(ItemStack stack, ResourceLocation itemId) {
        return !stack.isEmpty() && itemId.equals(itemId(stack));
    }

    public static void forEachManagedItem(Consumer<Item> consumer) {
        for (Item item : managedItems()) {
            consumer.accept(item);
        }
    }

    public static void forEachArmorOverrideItem(Consumer<Item> consumer) {
        for (Item item : armorOverrideItems()) {
            consumer.accept(item);
        }
    }

    @Nullable
    public static CuriosLogicalSlot resolveArmorOverrideLogicalSlot(ItemStack stack) {
        if (!isArmorOverrideItem(stack)) {
            return null;
        }
        return resolveLogicalSlot(stack);
    }

    private static List<Item> managedItems() {
        List<Item> cached = resolvedManagedItems;
        if (cached != null) {
            return cached;
        }

        synchronized (CuriosManagedItemRegistry.class) {
            if (resolvedManagedItems != null) {
                return resolvedManagedItems;
            }

            Set<Item> uniqueItems = new LinkedHashSet<>();
            for (ResourceLocation itemId : MANAGED_ITEM_SLOTS.keySet()) {
                Item item = ForgeRegistries.ITEMS.getValue(itemId);
                if (item != null) {
                    uniqueItems.add(item);
                }
            }
            resolvedManagedItems = List.copyOf(new ArrayList<>(uniqueItems));
            return resolvedManagedItems;
        }
    }

    private static List<Item> armorOverrideItems() {
        List<Item> cached = resolvedArmorOverrideItems;
        if (cached != null) {
            return cached;
        }

        synchronized (CuriosManagedItemRegistry.class) {
            if (resolvedArmorOverrideItems != null) {
                return resolvedArmorOverrideItems;
            }

            Set<Item> uniqueItems = new LinkedHashSet<>();
            for (ResourceLocation itemId : ARMOR_OVERRIDE_ITEM_IDS) {
                Item item = ForgeRegistries.ITEMS.getValue(itemId);
                if (item != null) {
                    uniqueItems.add(item);
                }
            }
            resolvedArmorOverrideItems = List.copyOf(new ArrayList<>(uniqueItems));
            return resolvedArmorOverrideItems;
        }
    }

    @Nullable
    private static ResourceLocation itemId(ItemStack stack) {
        return stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("jujutsucraft", path);
    }
}
