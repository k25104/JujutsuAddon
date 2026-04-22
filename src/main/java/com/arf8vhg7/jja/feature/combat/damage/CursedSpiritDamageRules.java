package com.arf8vhg7.jja.feature.combat.damage;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.jja.rct.RctMath;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CursedSpiritDamageRules {
    private static final String KEY_CURSED_SPIRIT = "CursedSpirit";
    private static final String KEY_JUJUTSU_SORCERER = "JujutsuSorcerer";
    private static final String KEY_CURSE_USER = "CurseUser";
    private static final String KEY_SHIKIGAMI = "Shikigami";
    private static final String KEY_CURSE_POWER = "CursePower";

    private static final TagKey<DamageType> CURSE_DAMAGE_TAG = TagKey.create(
        Registries.DAMAGE_TYPE,
        ResourceLocation.fromNamespaceAndPath("forge", "curse")
    );
    private static final TagKey<DamageType> COMBAT_DAMAGE_TAG = TagKey.create(
        Registries.DAMAGE_TYPE,
        ResourceLocation.fromNamespaceAndPath("forge", "combat")
    );
    private static final TagKey<EntityType<?>> NO_CURSE_POWER_ENTITY_TAG = TagKey.create(
        Registries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath("forge", "no_curse_power")
    );
    private static final TagKey<Item> CURSED_TOOL_ITEM_TAG = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("forge", "cursed_tool")
    );

    private CursedSpiritDamageRules() {
    }

    public static boolean shouldCancelAttack(LivingEntity target, DamageSource damageSource) {
        if (!isCursedSpiritTarget(target)) {
            return false;
        }

        if (isAdministrativeOrVoid(damageSource)) {
            return false;
        }

        if (isCursedEnergyDamage(damageSource)) {
            return false;
        }

        return isNaturalDamage(damageSource);
    }

    static boolean shouldCancelAttack(boolean targetCursedSpirit, boolean outOfWorld, boolean cursedEnergyDamage) {
        return CursedSpiritDamageDecisionRules.shouldCancelAttack(targetCursedSpirit, outOfWorld, cursedEnergyDamage);
    }

    static boolean shouldCancelAttack(boolean targetCursedSpirit, boolean administrativeOrVoid, boolean cursedEnergyDamage, boolean naturalDamage) {
        return CursedSpiritDamageDecisionRules.shouldCancelAttack(targetCursedSpirit, administrativeOrVoid, cursedEnergyDamage, naturalDamage);
    }

    public static boolean isCursedEnergyDamage(@Nullable DamageSource damageSource) {
        if (damageSource == null) {
            return false;
        }

        if (damageSource.is(CURSE_DAMAGE_TAG) || damageSource.is(COMBAT_DAMAGE_TAG)) {
            return true;
        }

        Entity directEntity = damageSource.getDirectEntity();
        Entity sourceEntity = damageSource.getEntity();
        return hasManualTechniqueMarker(directEntity)
            || hasManualTechniqueMarker(sourceEntity)
            || isCursedEnergyAttacker(sourceEntity)
            || isCursedEnergyAttacker(directEntity);
    }

    static boolean isCursedEnergyAttacker(
        boolean player,
        boolean playerHasCursePower,
        boolean heldCursedToolHasPower,
        boolean manualTechniqueProjectile,
        boolean jujutsucraftEntity,
        boolean noCursePowerTagged
    ) {
        return CursedSpiritDamageDecisionRules.isCursedEnergyAttacker(
            player,
            playerHasCursePower,
            heldCursedToolHasPower,
            manualTechniqueProjectile,
            jujutsucraftEntity,
            noCursePowerTagged
        );
    }

    static boolean isOutOfWorld(@Nullable DamageSource damageSource) {
        return damageSource != null && damageSource.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    static boolean isAdministrativeOrVoid(@Nullable DamageSource damageSource) {
        return damageSource != null
            && (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)
                || damageSource.is(DamageTypes.GENERIC_KILL)
                || damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
    }

    static boolean isNaturalDamage(@Nullable DamageSource damageSource) {
        if (damageSource == null || damageSource.getEntity() != null || damageSource.getDirectEntity() != null) {
            return false;
        }

        return damageSource.is(DamageTypeTags.IS_FIRE)
            || damageSource.is(DamageTypeTags.IS_FALL)
            || damageSource.is(DamageTypeTags.IS_DROWNING)
            || damageSource.is(DamageTypeTags.IS_FREEZING)
            || damageSource.is(DamageTypeTags.IS_LIGHTNING)
            || damageSource.is(DamageTypes.IN_WALL)
            || damageSource.is(DamageTypes.CRAMMING)
            || damageSource.is(DamageTypes.STARVE)
            || damageSource.is(DamageTypes.CACTUS)
            || damageSource.is(DamageTypes.FLY_INTO_WALL)
            || damageSource.is(DamageTypes.DRY_OUT)
            || damageSource.is(DamageTypes.SWEET_BERRY_BUSH)
            || damageSource.is(DamageTypes.STALAGMITE)
            || damageSource.is(DamageTypes.FALLING_BLOCK)
            || damageSource.is(DamageTypes.FALLING_ANVIL)
            || damageSource.is(DamageTypes.FALLING_STALACTITE)
            || damageSource.is(DamageTypes.OUTSIDE_BORDER);
    }

    private static boolean isCursedSpiritTarget(@Nullable Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_CURSED_SPIRIT);
    }

    private static boolean hasManualTechniqueMarker(@Nullable Entity entity) {
        return JjaJujutsucraftDataAccess.jjaIsManualTechniqueAttack(entity);
    }

    private static boolean isCursedEnergyAttacker(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }

        boolean heldCursedToolHasPower = entity instanceof LivingEntity livingEntity && hasPoweredCursedTool(livingEntity.getMainHandItem());
        return isCursedEnergyAttacker(
            entity instanceof Player,
            entity instanceof Player && JjaCursePowerAccountingService.hasEffectivePower(entity),
            heldCursedToolHasPower,
            hasManualTechniqueMarker(entity),
            isJujutsucraftEntity(entity),
            entity.getType().is(NO_CURSE_POWER_ENTITY_TAG)
        );
    }

    private static boolean hasPoweredCursedTool(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(CURSED_TOOL_ITEM_TAG) || !stack.hasTag()) {
            return false;
        }

        return stack.getOrCreateTag().getDouble(KEY_CURSE_POWER) > 0.0D;
    }

    private static boolean isJujutsucraftEntity(Entity entity) {
        ResourceLocation entityId = EntityType.getKey(entity.getType());
        return "jujutsucraft".equals(entityId.getNamespace())
            || entity.getPersistentData().getBoolean(KEY_JUJUTSU_SORCERER)
            || entity.getPersistentData().getBoolean(KEY_CURSE_USER)
            || entity.getPersistentData().getBoolean(KEY_CURSED_SPIRIT)
            || entity.getPersistentData().getBoolean(KEY_SHIKIGAMI)
            || RctMath.isCursedSpirit(entity);
    }
}
