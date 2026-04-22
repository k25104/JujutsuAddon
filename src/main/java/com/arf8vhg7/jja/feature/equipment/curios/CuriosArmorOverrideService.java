package com.arf8vhg7.jja.feature.equipment.curios;

import com.arf8vhg7.jja.compat.curios.JjaCuriosCompat;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class CuriosArmorOverrideService {
    private static final Map<Attribute, String> TRACKED_ATTRIBUTE_IDS = Map.of(
        Attributes.ARMOR, "armor",
        Attributes.ARMOR_TOUGHNESS, "armor_toughness",
        Attributes.KNOCKBACK_RESISTANCE, "knockback_resistance"
    );
    private static final Map<UUID, PlayerState> PLAYER_STATES = new java.util.HashMap<>();

    private CuriosArmorOverrideService() {
    }

    public static Multimap<Attribute, AttributeModifier> buildCuriosAttributeModifiers(
        @Nullable String identifier,
        UUID slotUuid,
        ItemStack stack
    ) {
        CuriosLogicalSlot logicalSlot = identifier == null ? null : CuriosLogicalSlot.fromCuriosIdentifier(identifier);

        if (logicalSlot == null || !CuriosManagedItemRegistry.isArmorOverrideForLogicalSlot(stack, logicalSlot)) {
            return ImmutableMultimap.of();
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        boolean hasTrackedModifier = false;

        for (Map.Entry<Attribute, AttributeModifier> entry : stack.getAttributeModifiers(logicalSlot.equipmentSlot()).entries()) {
            String trackedAttributeId = TRACKED_ATTRIBUTE_IDS.get(entry.getKey());
            if (trackedAttributeId == null) {
                continue;
            }

            AttributeModifier sourceModifier = entry.getValue();
            UUID derivedId = UUID.nameUUIDFromBytes(
                ("jja:curios:override:" + slotUuid + ":" + logicalSlot.name() + ":" + trackedAttributeId + ":"
                    + sourceModifier.getOperation().name() + ":" + sourceModifier.getId())
                        .getBytes(StandardCharsets.UTF_8)
            );
            builder.put(
                entry.getKey(),
                new AttributeModifier(
                    derivedId,
                    "jja.curios.override." + logicalSlot.name().toLowerCase(java.util.Locale.ROOT) + "." + trackedAttributeId,
                    sourceModifier.getAmount(),
                    sourceModifier.getOperation()
                )
            );
            hasTrackedModifier = true;
        }

        return hasTrackedModifier ? builder.build() : ImmutableMultimap.of();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean shouldSuppressVanillaArmorRender(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromEquipmentSlot(equipmentSlot);
        return logicalSlot != null && findArmorOverrideCuriosStack(livingEntity, logicalSlot).isPresent();
    }

    public static void markVanillaArmorDirty(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        CuriosLogicalSlot logicalSlot = CuriosLogicalSlot.fromEquipmentSlot(equipmentSlot);
        if (logicalSlot == null) {
            return;
        }
        PlayerState playerState = state(livingEntity);
        if (playerState != null) {
            playerState.dirty = true;
        }
    }

    public static void tick(LivingEntity livingEntity) {
        PlayerState playerState = state(livingEntity);
        if (playerState == null) {
            return;
        }

        PlayerSnapshot snapshot = captureSnapshot(livingEntity);
        if (!playerState.dirty && snapshot.sameAs(playerState.snapshot)) {
            return;
        }

        removeAppliedMasks(livingEntity, playerState);
        applyVanillaMasks(livingEntity, playerState, snapshot);
        playerState.snapshot = snapshot;
        playerState.dirty = false;
    }

    public static void clearRuntimeState(@Nullable Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        PlayerState playerState = PLAYER_STATES.remove(livingEntity.getUUID());
        if (playerState == null) {
            return;
        }
        removeAppliedMasks(livingEntity, playerState);
    }

    private static void applyVanillaMasks(LivingEntity livingEntity, PlayerState playerState, PlayerSnapshot snapshot) {
        applyVanillaMaskForSlot(livingEntity, playerState, CuriosLogicalSlot.HEAD, snapshot.headCurio(), snapshot.headVanilla());
        applyVanillaMaskForSlot(livingEntity, playerState, CuriosLogicalSlot.BODY, snapshot.bodyCurio(), snapshot.bodyVanilla());
        applyVanillaMaskForSlot(livingEntity, playerState, CuriosLogicalSlot.LEGS, snapshot.legsCurio(), snapshot.legsVanilla());
    }

    private static void applyVanillaMaskForSlot(
        LivingEntity livingEntity,
        PlayerState playerState,
        CuriosLogicalSlot logicalSlot,
        ItemStack curiosStack,
        ItemStack vanillaStack
    ) {
        if (curiosStack.isEmpty() || vanillaStack.isEmpty()) {
            return;
        }

        for (Map.Entry<Attribute, AttributeModifier> entry : vanillaStack.getAttributeModifiers(logicalSlot.equipmentSlot()).entries()) {
            String trackedAttributeId = TRACKED_ATTRIBUTE_IDS.get(entry.getKey());
            if (trackedAttributeId == null) {
                continue;
            }

            AttributeInstance attributeInstance = livingEntity.getAttribute(entry.getKey());
            if (attributeInstance == null) {
                continue;
            }

            AttributeModifier sourceModifier = entry.getValue();
            UUID derivedId = UUID.nameUUIDFromBytes(
                ("jja:curios:mask:" + logicalSlot.name() + ":" + trackedAttributeId + ":"
                    + sourceModifier.getOperation().name() + ":" + sourceModifier.getId())
                        .getBytes(StandardCharsets.UTF_8)
            );
            attributeInstance.removeModifier(derivedId);
            attributeInstance.addTransientModifier(
                new AttributeModifier(
                    derivedId,
                    "jja.curios.mask." + logicalSlot.name().toLowerCase(java.util.Locale.ROOT) + "." + trackedAttributeId,
                    -sourceModifier.getAmount(),
                    sourceModifier.getOperation()
                )
            );
            playerState.appliedMasks.add(new AppliedMask(entry.getKey(), derivedId));
        }
    }

    private static void removeAppliedMasks(LivingEntity livingEntity, PlayerState playerState) {
        for (AppliedMask appliedMask : playerState.appliedMasks) {
            AttributeInstance attributeInstance = livingEntity.getAttribute(appliedMask.attribute());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(appliedMask.modifierId());
            }
        }
        playerState.appliedMasks.clear();
    }

    private static PlayerSnapshot captureSnapshot(LivingEntity livingEntity) {
        return new PlayerSnapshot(
            snapshot(findArmorOverrideCuriosStack(livingEntity, CuriosLogicalSlot.HEAD).orElse(ItemStack.EMPTY)),
            snapshot(findArmorOverrideCuriosStack(livingEntity, CuriosLogicalSlot.BODY).orElse(ItemStack.EMPTY)),
            snapshot(findArmorOverrideCuriosStack(livingEntity, CuriosLogicalSlot.LEGS).orElse(ItemStack.EMPTY)),
            snapshot(livingEntity.getItemBySlot(EquipmentSlot.HEAD)),
            snapshot(livingEntity.getItemBySlot(EquipmentSlot.CHEST)),
            snapshot(livingEntity.getItemBySlot(EquipmentSlot.LEGS))
        );
    }

    private static Optional<ItemStack> findArmorOverrideCuriosStack(LivingEntity livingEntity, CuriosLogicalSlot logicalSlot) {
        return JjaCuriosCompat.findManagedStack(
            livingEntity,
            logicalSlot.curiosIdentifier(),
            stack -> CuriosManagedItemRegistry.isArmorOverrideForLogicalSlot(stack, logicalSlot)
        );
    }

    private static ItemStack snapshot(ItemStack stack) {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    @Nullable
    private static PlayerState state(LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide() || !JjaCuriosCompat.isCuriosLoaded()) {
            return null;
        }
        return PLAYER_STATES.computeIfAbsent(livingEntity.getUUID(), ignored -> new PlayerState());
    }

    private record AppliedMask(Attribute attribute, UUID modifierId) {
    }

    private record PlayerSnapshot(
        ItemStack headCurio,
        ItemStack bodyCurio,
        ItemStack legsCurio,
        ItemStack headVanilla,
        ItemStack bodyVanilla,
        ItemStack legsVanilla
    ) {
        private boolean sameAs(@Nullable PlayerSnapshot other) {
            return other != null
                && ItemStack.matches(this.headCurio, other.headCurio)
                && ItemStack.matches(this.bodyCurio, other.bodyCurio)
                && ItemStack.matches(this.legsCurio, other.legsCurio)
                && ItemStack.matches(this.headVanilla, other.headVanilla)
                && ItemStack.matches(this.bodyVanilla, other.bodyVanilla)
                && ItemStack.matches(this.legsVanilla, other.legsVanilla);
        }
    }

    private static final class PlayerState {
        @Nullable
        private PlayerSnapshot snapshot;
        private boolean dirty;
        private final Set<AppliedMask> appliedMasks = new LinkedHashSet<>();
    }
}
