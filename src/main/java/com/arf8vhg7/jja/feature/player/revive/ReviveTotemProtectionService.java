package com.arf8vhg7.jja.feature.player.revive;

import javax.annotation.Nullable;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class ReviveTotemProtectionService {
    private ReviveTotemProtectionService() {
    }

    static boolean shouldDeferToTotemProtection(@Nullable LivingEntity livingEntity, @Nullable Object source) {
        if (!(source instanceof DamageSource damageSource)) {
            return shouldDeferToTotemProtection(hasTotemInHands(livingEntity), false, false);
        }
        return shouldDeferToTotemProtection(livingEntity, damageSource);
    }

    static boolean shouldDeferToTotemProtection(@Nullable LivingEntity livingEntity, @Nullable DamageSource damageSource) {
        return shouldDeferToTotemProtection(
            hasTotemInHands(livingEntity),
            damageSource != null,
            damageSource != null && damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
        );
    }

    static boolean shouldDeferToTotemProtection(boolean hasTotemInHands, boolean knownDamageSource, boolean bypassesInvulnerability) {
        if (!hasTotemInHands) {
            return false;
        }
        if (!knownDamageSource) {
            return true;
        }
        return !bypassesInvulnerability;
    }

    static boolean shouldStartWaiting(boolean canEnterWaiting, boolean shouldDeferToTotemProtection) {
        return canEnterWaiting && !shouldDeferToTotemProtection;
    }

    static boolean hasTotemInHands(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null) {
            return false;
        }
        return hasTotemInHands(isTotem(livingEntity.getMainHandItem()), isTotem(livingEntity.getOffhandItem()));
    }

    static boolean hasTotemInHands(boolean mainHandTotem, boolean offHandTotem) {
        return mainHandTotem || offHandTotem;
    }

    private static boolean isTotem(ItemStack stack) {
        return stack != null && stack.is(Items.TOTEM_OF_UNDYING);
    }
}
