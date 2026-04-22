package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.CopiedTechniqueInventoryLimit;
import com.arf8vhg7.jja.feature.jja.technique.family.mahoraga.MahoragaAdaptation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class EffectCharactorProcedureHook {
    private EffectCharactorProcedureHook() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static Component buildCopiedTechniqueHoverName(LevelAccessor world, Entity entity, ItemStack itemStack, Component fallback) {
        if (world == null || entity == null) {
            return fallback;
        }
        Entity owner = entity.getType()
            .is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("forge", "ranged_ammo")))
                ? JjaJujutsucraftDataAccess.jjaResolveDirectOwner(world, entity)
                : entity;
        if (!(owner instanceof LivingEntity)) {
            owner = entity;
        }
        long cooldown = Math.round(itemStack.getOrCreateTag().getDouble("COOLDOWN_TICKS"));
        return Component.empty()
            .append(owner.getDisplayName())
            .append(Component.translatable("jujutsu.message.cursed_technique"))
            .append(Component.literal(" ("))
            .append(Component.translatable("jujutsu.overlay.cost"))
            .append(Component.literal(": " + cooldown + ")"));
    }

    public static double resolveAdaptationProgress(Entity entity, Entity sourceEntity, String key, double originalValue) {
        return MahoragaAdaptation.resolveRegistrationProgress(entity, key, originalValue, sourceEntity, true);
    }

    public static boolean shouldDisplayAdaptationStart(Entity entity) {
        return MahoragaAdaptation.consumeStartMessageFlag(entity);
    }

    public static boolean shouldGiveCopiedTechnique(Player player, ItemStack itemStack) {
        return CopiedTechniqueInventoryLimit.canGiveCopiedTechnique(player, itemStack);
    }
}
