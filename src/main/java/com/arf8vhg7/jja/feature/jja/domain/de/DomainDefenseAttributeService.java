package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueService;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class DomainDefenseAttributeService {
    private static final ManagedModifier SIMPLE_DOMAIN_ARMOR = new ManagedModifier(
        DefenseProfile.SIMPLE_DOMAIN,
        ManagedAttribute.ARMOR,
        UUID.fromString("f3f83630-6d13-4f49-b2c1-75f6f6ae0001"),
        "jja.domain_defense.simple_domain.armor",
        2.0D,
        Operation.ADDITION
    );
    private static final ManagedModifier SIMPLE_DOMAIN_ARMOR_TOUGHNESS = new ManagedModifier(
        DefenseProfile.SIMPLE_DOMAIN,
        ManagedAttribute.ARMOR_TOUGHNESS,
        UUID.fromString("f3f83630-6d13-4f49-b2c1-75f6f6ae0002"),
        "jja.domain_defense.simple_domain.armor_toughness",
        1.0D,
        Operation.ADDITION
    );
    private static final ManagedModifier DOMAIN_EXPANSION_ARMOR = new ManagedModifier(
        DefenseProfile.DOMAIN_EXPANSION,
        ManagedAttribute.ARMOR,
        UUID.fromString("f3f83630-6d13-4f49-b2c1-75f6f6ae0003"),
        "jja.domain_defense.domain_expansion.armor",
        4.0D,
        Operation.ADDITION
    );
    private static final ManagedModifier DOMAIN_EXPANSION_ARMOR_TOUGHNESS = new ManagedModifier(
        DefenseProfile.DOMAIN_EXPANSION,
        ManagedAttribute.ARMOR_TOUGHNESS,
        UUID.fromString("f3f83630-6d13-4f49-b2c1-75f6f6ae0004"),
        "jja.domain_defense.domain_expansion.armor_toughness",
        2.0D,
        Operation.ADDITION
    );
    private static final List<ManagedModifier> SIMPLE_DOMAIN_MODIFIERS = List.of(
        SIMPLE_DOMAIN_ARMOR,
        SIMPLE_DOMAIN_ARMOR_TOUGHNESS
    );
    private static final List<ManagedModifier> DOMAIN_EXPANSION_MODIFIERS = List.of(
        DOMAIN_EXPANSION_ARMOR,
        DOMAIN_EXPANSION_ARMOR_TOUGHNESS
    );
    private static final List<ManagedModifier> ALL_MODIFIERS = List.of(
        SIMPLE_DOMAIN_ARMOR,
        SIMPLE_DOMAIN_ARMOR_TOUGHNESS,
        DOMAIN_EXPANSION_ARMOR,
        DOMAIN_EXPANSION_ARMOR_TOUGHNESS
    );

    private DomainDefenseAttributeService() {
    }

    public static void sync(LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide() || !shouldManage(livingEntity)) {
            return;
        }

        applyProfile(livingEntity, resolveProfile(livingEntity, null));
    }

    public static void syncAfterRemoval(LivingEntity livingEntity, MobEffect removedEffect) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        applyProfile(livingEntity, resolveProfile(livingEntity, removedEffect));
    }

    static DefenseProfile resolveProfile(boolean hasDomainExpansionEffect, boolean hasSimpleDomainEffect, boolean suppressSimpleDomainBonus) {
        return resolveProfile(hasDomainExpansionEffect, hasSimpleDomainEffect, hasSimpleDomainEffect, suppressSimpleDomainBonus);
    }

    static DefenseProfile resolveProfile(
        boolean hasDomainExpansionEffect,
        boolean hasSimpleDomainEffect,
        boolean selfOwnedSimpleDomain,
        boolean suppressSimpleDomainBonus
    ) {
        if (hasDomainExpansionEffect) {
            return DefenseProfile.DOMAIN_EXPANSION;
        }
        if (hasSimpleDomainEffect && selfOwnedSimpleDomain && !suppressSimpleDomainBonus) {
            return DefenseProfile.SIMPLE_DOMAIN;
        }
        return DefenseProfile.NONE;
    }

    static List<ManagedModifier> modifiersFor(DefenseProfile profile) {
        return switch (profile) {
            case NONE -> List.of();
            case SIMPLE_DOMAIN -> SIMPLE_DOMAIN_MODIFIERS;
            case DOMAIN_EXPANSION -> DOMAIN_EXPANSION_MODIFIERS;
        };
    }

    static List<ManagedModifier> allModifiers() {
        return ALL_MODIFIERS;
    }

    private static DefenseProfile resolveProfile(LivingEntity livingEntity, @Nullable MobEffect removedEffect) {
        return resolveProfile(
            hasRelevantEffect(livingEntity, JujutsucraftModMobEffects.DOMAIN_EXPANSION.get(), removedEffect),
            hasRelevantEffect(livingEntity, JujutsucraftModMobEffects.SIMPLE_DOMAIN.get(), removedEffect),
            AntiDomainTechniqueService.hasOwnedSimpleDomain(livingEntity),
            AntiDomainTechniqueService.shouldSuppressSimpleDomainDerivedEffects(livingEntity)
        );
    }

    private static boolean shouldManage(LivingEntity livingEntity) {
        return livingEntity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get())
            || livingEntity.hasEffect(JujutsucraftModMobEffects.SIMPLE_DOMAIN.get())
            || hasManagedModifier(livingEntity);
    }

    private static boolean hasRelevantEffect(LivingEntity livingEntity, MobEffect effect, @Nullable MobEffect removedEffect) {
        return effect != removedEffect && livingEntity.hasEffect(effect);
    }

    private static boolean hasManagedModifier(LivingEntity livingEntity) {
        for (ManagedModifier managedModifier : ALL_MODIFIERS) {
            AttributeInstance attributeInstance = livingEntity.getAttribute(managedModifier.resolveAttribute());
            if (attributeInstance != null && attributeInstance.getModifier(managedModifier.id()) != null) {
                return true;
            }
        }
        return false;
    }

    private static void applyProfile(LivingEntity livingEntity, DefenseProfile profile) {
        for (ManagedModifier managedModifier : ALL_MODIFIERS) {
            AttributeInstance attributeInstance = livingEntity.getAttribute(managedModifier.resolveAttribute());
            if (attributeInstance == null) {
                continue;
            }

            if (managedModifier.profile() == profile) {
                ensureModifier(attributeInstance, managedModifier);
                continue;
            }

            attributeInstance.removeModifier(managedModifier.id());
        }
    }

    private static void ensureModifier(AttributeInstance attributeInstance, ManagedModifier managedModifier) {
        AttributeModifier existingModifier = attributeInstance.getModifier(managedModifier.id());
        if (existingModifier != null
            && Double.compare(existingModifier.getAmount(), managedModifier.amount()) == 0
            && existingModifier.getOperation() == managedModifier.operation()) {
            return;
        }

        attributeInstance.removeModifier(managedModifier.id());
        attributeInstance.addTransientModifier(managedModifier.toAttributeModifier());
    }

    enum DefenseProfile {
        NONE,
        SIMPLE_DOMAIN,
        DOMAIN_EXPANSION
    }

    enum ManagedAttribute {
        ARMOR,
        ARMOR_TOUGHNESS;

        net.minecraft.world.entity.ai.attributes.Attribute resolve() {
            return switch (this) {
                case ARMOR -> Attributes.ARMOR;
                case ARMOR_TOUGHNESS -> Attributes.ARMOR_TOUGHNESS;
            };
        }
    }

    record ManagedModifier(
        DefenseProfile profile,
        ManagedAttribute attribute,
        UUID id,
        String name,
        double amount,
        Operation operation
    ) {
        net.minecraft.world.entity.ai.attributes.Attribute resolveAttribute() {
            return attribute.resolve();
        }

        AttributeModifier toAttributeModifier() {
            return new AttributeModifier(id, name, amount, operation);
        }
    }
}
