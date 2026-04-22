package com.arf8vhg7.jja.feature.jja.traits.sixeyes;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

final class SixEyesSummonTargetRules {
    private static final String KEY_DOMAIN_ENTITY = "domain_entity";
    private static final String KEY_SHIKIGAMI = "Shikigami";
    private static final String KEY_CURSED_SPIRIT = "CursedSpirit";
    private static final TagKey<EntityType<?>> TEN_SHADOWS_TECHNIQUE_TAG = TagKey.create(
        Objects.requireNonNull(Registries.ENTITY_TYPE),
        Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath("jujutsucraft", "ten_shadows_technique"))
    );
    private SixEyesSummonTargetRules() {
    }

    static boolean isOwnedSummon(Player player, LivingEntity target) {
        if (player == null || target == null || target == player || target.getPersistentData().getBoolean(KEY_DOMAIN_ENTITY)) {
            return false;
        }
        Entity rootOwner = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(player.level(), target);
        if (!(rootOwner instanceof Player rootPlayer) || !player.getUUID().equals(rootPlayer.getUUID())) {
            return false;
        }
        return isRecognizedSummon(target);
    }

    static boolean isRecognizedSummon(LivingEntity target) {
        if (target.getPersistentData().getBoolean(KEY_SHIKIGAMI)
            || target.getPersistentData().getBoolean(KEY_CURSED_SPIRIT)
            || target.getType().is(Objects.requireNonNull(TEN_SHADOWS_TECHNIQUE_TAG))) {
            return true;
        }
        return isRecognizedSummonPath(resolveEntityPath(target.getType()));
    }

    private static String resolveEntityPath(EntityType<?> entityType) {
        String descriptionId = entityType.getDescriptionId();
        int lastDot = descriptionId.lastIndexOf('.');
        return lastDot < 0 ? descriptionId : descriptionId.substring(lastDot + 1);
    }

    static boolean isRecognizedSummonPath(String entityPath) {
        return SixEyesSummonPathRules.isRecognizedSummonPath(entityPath);
    }
}