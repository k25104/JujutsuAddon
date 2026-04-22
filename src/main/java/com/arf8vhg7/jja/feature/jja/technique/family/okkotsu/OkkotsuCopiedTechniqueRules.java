package com.arf8vhg7.jja.feature.jja.technique.family.okkotsu;

import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OkkotsuCopiedTechniqueRules {
    private static final String USED_KEY = "Used";

    private OkkotsuCopiedTechniqueRules() {
    }

    public static void grantMegaphone(@Nullable Player player) {
        if (player == null) {
            return;
        }

        ItemStack loudspeaker = new ItemStack(JujutsucraftModItems.LOUDSPEAKER.get());
        if (resolveGrantTarget(player.getOffhandItem().isEmpty()) == LoudspeakerGrantTarget.OFFHAND) {
            player.setItemInHand(InteractionHand.OFF_HAND, loudspeaker);
            return;
        }

        boolean added = player.getInventory().add(loudspeaker);
        if (!added && !loudspeaker.isEmpty()) {
            player.drop(loudspeaker, false);
        }
    }

    public static boolean hasUnusedHeldLoudspeaker(@Nullable Entity entity) {
        return findUnusedHeldLoudspeaker(entity) != null;
    }

    @Nullable
    public static ItemStack findUnusedHeldLoudspeaker(@Nullable Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }

        return switch (resolveHeldLoudspeakerHand(
            isUnusedLoudspeaker(livingEntity.getMainHandItem()),
            isUnusedLoudspeaker(livingEntity.getOffhandItem())
        )) {
            case MAIN -> livingEntity.getMainHandItem();
            case OFFHAND -> livingEntity.getOffhandItem();
            case NONE -> null;
        };
    }

    public static ItemStack resolveHeldTechniqueStack(@Nullable Entity entity, @Nullable ItemStack defaultStack) {
        ItemStack loudspeaker = findUnusedHeldLoudspeaker(entity);
        if (loudspeaker != null) {
            return loudspeaker;
        }
        return defaultStack == null ? ItemStack.EMPTY : defaultStack;
    }

    public static boolean markHeldLoudspeakerUsed(@Nullable Entity entity) {
        ItemStack loudspeaker = findUnusedHeldLoudspeaker(entity);
        if (loudspeaker == null) {
            return false;
        }

        loudspeaker.getOrCreateTag().putBoolean(USED_KEY, true);
        return true;
    }

    public static boolean isUnusedLoudspeaker(@Nullable ItemStack itemStack) {
        return itemStack != null
            && !itemStack.isEmpty()
            && itemStack.getItem() == JujutsucraftModItems.LOUDSPEAKER.get()
            && !isMarkedUsed(itemStack);
    }

    static LoudspeakerGrantTarget resolveGrantTarget(boolean offhandEmpty) {
        return offhandEmpty ? LoudspeakerGrantTarget.OFFHAND : LoudspeakerGrantTarget.INVENTORY;
    }

    static HeldLoudspeakerHand resolveHeldLoudspeakerHand(boolean mainUnusedLoudspeaker, boolean offUnusedLoudspeaker) {
        if (mainUnusedLoudspeaker) {
            return HeldLoudspeakerHand.MAIN;
        }
        if (offUnusedLoudspeaker) {
            return HeldLoudspeakerHand.OFFHAND;
        }
        return HeldLoudspeakerHand.NONE;
    }

    private static boolean isMarkedUsed(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag() != null && itemStack.getTag().getBoolean(USED_KEY);
    }

    enum LoudspeakerGrantTarget {
        OFFHAND,
        INVENTORY
    }

    enum HeldLoudspeakerHand {
        NONE,
        MAIN,
        OFFHAND
    }
}
