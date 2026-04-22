package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionDamageBoost;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainDamageBoost;
import com.arf8vhg7.jja.feature.combat.zone.ZoneEffectOverrides;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class DamageFixProcedureHook {
    private DamageFixProcedureHook() {
    }

    public static boolean shouldApplyZoneDamageBoost(boolean original) {
        return original && ZoneEffectOverrides.shouldApplyDamageBoost();
    }

    public static void applyDomainDamage(Entity entity) {
        double originalDamage = entity.getPersistentData().getDouble("Damage");
        entity.getPersistentData().putDouble("Damage", originalDamage * resolveDamageMultiplier(entity));
    }

    public static double jja$stripHeldItemAttackDamageBonus(Entity entity, double attackDamage, boolean extraArmAttack) {
        if (!extraArmAttack || !(entity instanceof Player player)) {
            return attackDamage;
        }

        return jja$stripHeldItemAttackDamageBonus(attackDamage, resolveMainHandAttackDamageBonus(player.getMainHandItem()));
    }

    static double jja$stripHeldItemAttackDamageBonus(double attackDamage, double attackDamageBonus) {
        return attackDamage - attackDamageBonus;
    }

    static double resolveTwinnedBodyEchoSnapshotDamage(
        double damage,
        double currentAttackDamage,
        double heldItemAttackDamageBonus,
        double heldItemDamageMultiplier,
        double strengthEffectBonus,
        double weaknessEffectPenalty
    ) {
        double normalizedHeldItemDamageMultiplier = heldItemDamageMultiplier > 0.0D ? heldItemDamageMultiplier : 1.0D;
        double currentStrengthMultiplier = resolveStrengthDamageMultiplier(
            currentAttackDamage,
            strengthEffectBonus,
            weaknessEffectPenalty
        );
        double bareHandStrengthMultiplier = resolveStrengthDamageMultiplier(
            currentAttackDamage - heldItemAttackDamageBonus,
            strengthEffectBonus,
            weaknessEffectPenalty
        );

        if (currentStrengthMultiplier <= 0.0D) {
            return Math.max(0.0D, damage / normalizedHeldItemDamageMultiplier);
        }

        return Math.max(
            0.0D,
            damage / normalizedHeldItemDamageMultiplier / currentStrengthMultiplier * bareHandStrengthMultiplier
        );
    }

    static double resolveStrengthDamageMultiplier(double attackDamage, double strengthEffectBonus, double weaknessEffectPenalty) {
        double strengthLevel = attackDamage * 0.333D + strengthEffectBonus - weaknessEffectPenalty;
        return 1.0D + strengthLevel * 0.333D;
    }

    static double resolveHeldItemDamageMultiplier(ItemStack mainHandItem, double cnt6) {
        if (mainHandItem.isEmpty()) {
            return 1.0D;
        }

        double multiplier = 1.0D;
        var tag = mainHandItem.getTag();
        double power = tag == null ? 0.0D : tag.getDouble("Power");
        if (power > 0.0D) {
            multiplier *= 1.0D + power * Math.max(Math.min(cnt6 * 0.2D, 1.0D), 0.0D);
        } else if (power < 0.0D) {
            multiplier *= 1.0D + power;
        }

        if (mainHandItem.getItem() == JujutsucraftModItems.PLAYFUL_CLOUD.get()) {
            multiplier *= 1.25D;
        }

        return multiplier;
    }

    static double resolveDamageMultiplier(Entity entity) {
        return resolveDamageMultiplier(
            DomainExpansionDamageBoost.resolveMultiplier(entity),
            SimpleDomainDamageBoost.resolveMultiplier(entity)
        );
    }

    static double resolveDamageMultiplier(double domainExpansionMultiplier, double simpleDomainMultiplier) {
        return Math.max(domainExpansionMultiplier, simpleDomainMultiplier);
    }

    static double resolveMainHandAttackDamageBonus(ItemStack mainHandItem) {
        double attackDamageBonus = 0.0D;
        for (var entry : mainHandItem.getAttributeModifiers(EquipmentSlot.MAINHAND).entries()) {
            if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
                attackDamageBonus += entry.getValue().getAmount();
            }
        }

        return attackDamageBonus;
    }
}
