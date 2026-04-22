package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import javax.annotation.Nullable;

public final class PlayerHandStateRules {
    private static final int TODO_CT = 20;
    private static final int MAHITO_CT = 15;

    private PlayerHandStateRules() {
    }

    public static boolean isHandOccupied(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    public static HandItemState classifyHandItem(@Nullable Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return HandItemState.EMPTY;
        }

        if (isBareHandEquivalent(player, stack)) {
            return HandItemState.BARE_HAND_EQUIVALENT;
        }

        return isSlashWeapon(stack) ? HandItemState.SLASH_WEAPON : HandItemState.TAIJUTSU_WEAPON;
    }

    public static boolean isTaijutsuWeapon(@Nullable Player player, ItemStack stack) {
        return classifyHandItem(player, stack).isTaijutsuWeapon();
    }

    public static boolean isBareHandEquivalent(@Nullable Player player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        if (item == JujutsucraftModItems.ITADORI_ARM.get()
            || item == JujutsucraftModItems.COPIED_CURSED_TECHNIQUE.get()
            || item == JujutsucraftModItems.CURSED_TECHNIQUE_STARTER.get()) {
            return true;
        }

        if (item == JujutsucraftModItems.PENDANT_TODO_AOI.get() && TechniqueSkillResolver.hasTechnique(player, TODO_CT)) {
            return true;
        }

        return (item == JujutsucraftModItems.MAHITO_HAND_1.get() || item == JujutsucraftModItems.MAHITO_HAND_2.get())
            && TechniqueSkillResolver.hasTechnique(player, MAHITO_CT);
    }

    public static boolean isSlashWeapon(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        if (item == JujutsucraftModItems.GARUDA_ITEM.get() || item == JujutsucraftModItems.GARUDA_ITEM_BALL.get()) {
            return false;
        }
        if (item instanceof SwordItem
            || item instanceof AxeItem
            || item instanceof PickaxeItem
            || item instanceof ShovelItem
            || item instanceof HoeItem) {
            return true;
        }
        return item == JujutsucraftModItems.FESTER_LIFE_BLADE.get()
            || item == JujutsucraftModItems.NAGINATA.get()
            || item == JujutsucraftModItems.SLAUGHTER_DEMON.get()
            || item == JujutsucraftModItems.INVERTED_SPEAR_OF_HEAVEN.get()
            || item == JujutsucraftModItems.SUPREME_MARTIAL_SOLUTION.get()
            || item == JujutsucraftModItems.KNIFE.get()
            || item == JujutsucraftModItems.SCISSORS.get()
            || item == JujutsucraftModItems.CLAWS.get()
            || item == JujutsucraftModItems.SICKLE.get()
            || item == JujutsucraftModItems.HITEN.get()
            || item == JujutsucraftModItems.SPLIT_SOUL_KATANA.get()
            || item == JujutsucraftModItems.SWORD_BLACK.get();
    }
}