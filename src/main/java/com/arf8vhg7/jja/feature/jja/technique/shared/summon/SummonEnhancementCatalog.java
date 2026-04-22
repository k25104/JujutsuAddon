package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvEnhancementRules;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiSummonBranchResolver;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import java.util.Objects;
import java.util.Set;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.minecraft.nbt.Tag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SummonEnhancementCatalog {
    private static final ResourceKey<Level> CURSED_SPIRIT_MANIPULATION_DIMENSION = ResourceKey.create(
        Objects.requireNonNull(Registries.DIMENSION),
        Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath("jujutsucraft", "cursed_spirit_manipulation_dimension"))
    );
    private static final String JJA_SUMMON_BASE_MAX_HEALTH = "jjaSummonEnhancementBaseMaxHealth";
    private static final String NUE_TOTALITY_NAME = "entity.jujutsucraft.nue_totality";
    private static final String SHIFT_MAX_ELEPHANT_TECHNIQUE_NAME = "jujutsu.technique.choso3";
    private static final String MAHORAGA_WHEEL_NAME = "item.jujutsucraft.mahoraga_wheel_helmet";

    private SummonEnhancementCatalog() {
    }

    public static boolean hasToggleableSkillForActiveCt(ServerPlayer player) {
        return switch (JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player)) {
            case 6, 8, 9, 18, 35, 37, 40, 43 -> true;
            default -> false;
        };
    }

    public static ResolvedSummon resolve(ServerPlayer player, int activeCtId, int selectedTechnique, String currentTechniqueName) {
        if (player == null || activeCtId <= 0 || selectedTechnique <= 0) {
            return null;
        }
        if (MegumiSummonBranchResolver.isUntamedSelection(player, activeCtId, selectedTechnique, currentTechniqueName)) {
            return null;
        }
        String resolvedName = RegisteredCurseTechniqueSlots.resolveRegisteredTechniqueName(player, currentTechniqueName);
        int skillId = activeCtId * 100 + selectedTechnique;
        int strengthAmplifier = getStrengthAmplifier(player);
        return switch (skillId) {
            case 3505 -> single(skillId, resolvedName, 100.0, JujutsucraftModEntities.MOON_DREGS.get(), 80L);
            case 605 -> single(skillId, resolvedName, 60.0, JujutsucraftModEntities.DIVINE_DOG_WHITE.get(), 120L);
            case 606 -> single(skillId, resolvedName, 60.0, JujutsucraftModEntities.DIVINE_DOG_BLACK.get(), 120L);
            case 607 -> single(skillId, resolvedName, 150.0, JujutsucraftModEntities.DIVINE_DOG_TOTALITY.get(), 120L);
            case 608 -> single(
                skillId,
                resolvedName,
                NUE_TOTALITY_NAME.equals(resolvedName) ? 125.0 * 2.5 : 125.0,
                JujutsucraftModEntities.NUE.get(),
                120L
            );
            case 609 -> single(skillId, resolvedName, 200.0, JujutsucraftModEntities.GREAT_SERPENT.get(), 120L);
            case 610 -> single(skillId, resolvedName, 65.0, JujutsucraftModEntities.TOAD.get(), 120L);
            case 611 -> SHIFT_MAX_ELEPHANT_TECHNIQUE_NAME.equals(resolvedName)
                ? null
                : single(skillId, resolvedName, 300.0, JujutsucraftModEntities.MAX_ELEPHANT.get(), 120L);
            case 613 -> single(skillId, resolvedName, 200.0, JujutsucraftModEntities.ROUND_DEER.get(), 120L);
            case 614 -> single(skillId, resolvedName, 220.0, JujutsucraftModEntities.PIERCING_OX.get(), 120L);
            case 615 -> single(skillId, resolvedName, 180.0, JujutsucraftModEntities.TIGER_FUNERAL.get(), 120L);
            case 617 -> single(skillId, resolvedName, 280.0, JujutsucraftModEntities.MERGED_BEAST_AGITO.get(), 120L);
            case 618 -> MAHORAGA_WHEEL_NAME.equals(resolvedName)
                ? null
                : single(
                    skillId,
                    resolvedName,
                    480.0,
                    JujutsucraftModEntities.EIGHT_HANDLED_SWORD_DIVERGENT_SILA_DIVINE_GENERAL_MAHORAGA.get(),
                    120L
                );
            case 807 -> single(
                skillId,
                resolvedName,
                (20.0 + strengthAmplifier * 5.0) * 1.25,
                JujutsucraftModEntities.SEA_SERPENT.get(),
                40L
            );
            case 810 -> multi(skillId, resolvedName, 100.0 * 2.0, 2, JujutsucraftModEntities.BATHYNOMUS_GIGANTEUS.get(), 40L);
            case 906 -> single(skillId, resolvedName, 100.0, JujutsucraftModEntities.GARUDA.get(), 40L);
            case 1811, 1812, 1813 -> resolveGeto(player, skillId, resolvedName);
            case 3705 -> single(skillId, resolvedName, 300.0, JujutsucraftModEntities.SHIKIGAMI_HETEROCEPHALUS_GLABER.get(), 40L);
            case 3706 -> resolveDhruvPterosaurSummon(skillId, resolvedName);
            case 4008 -> single(
                skillId,
                resolvedName,
                150.0 + strengthAmplifier * 4.0,
                JujutsucraftModEntities.RYU.get(),
                80L
            );
            case 4305 -> multi(skillId, resolvedName, 80.0 * 3.0, 3, JujutsucraftModEntities.ROZETSU_SHIKIGAMI.get(), 40L);
            case 4306 -> single(skillId, resolvedName, 120.0, JujutsucraftModEntities.ROZETSU_SHIKIGAMI_VESSEL.get(), 40L);
            case 4307 -> single(skillId, resolvedName, 150.0, JujutsucraftModEntities.ROZETSU_SHIKIGAMI_VESSEL_2.get(), 40L);
            default -> null;
        };
    }

    static ResolvedSummon resolveDhruvPterosaurSummon(int skillId, String resolvedName) {
        DhruvEnhancementRules.PreviewConfig previewConfig = DhruvEnhancementRules.resolvePterosaurPreview();
        return single(
            skillId,
            resolvedName,
            previewConfig.activationBaseMaxHp(),
            JujutsucraftModEntities.SHIKIGAMI_PTEROSAUR.get(),
            previewConfig.pendingValidityTicks()
        );
    }

    private static ResolvedSummon resolveGeto(ServerPlayer player, int skillId, String resolvedName) {
        if (resolvedName == null || resolvedName.isEmpty()) {
            return null;
        }
        String slotRegisteredName = RegisteredCurseTechniqueSlots.resolveGetoRegisteredTechniqueName(player);
        boolean slotKeyPath = slotRegisteredName != null;
        String comparisonName = Objects.requireNonNull(slotKeyPath ? slotRegisteredName : resolvedName);
        ServerLevel manipulationLevel = player.serverLevel().getServer().getLevel(Objects.requireNonNull(CURSED_SPIRIT_MANIPULATION_DIMENSION));
        if (manipulationLevel == null) {
            return null;
        }
        double friendNum = JjaJujutsucraftDataAccess.jjaGetFriendNum(player);
        for (int index = 1; index <= 10000; index++) {
            String slotKey = "data_cursed_spirit_manipulation" + index;
            if (player.getPersistentData().getDouble(slotKey) == 0.0) {
                break;
            }
            String storedName = RegisteredCurseTechniqueSlots.normalizeGetoTechniqueName(player.getPersistentData().getString(slotKey + "_name"));
            double storedCount = player.getPersistentData().getDouble(slotKey + "_num");
            if (slotKeyPath) {
                if (!comparisonName.equals(storedName)) {
                    continue;
                }
            } else {
                String displayName = storedName + " ×" + Math.round(storedCount);
                if (!comparisonName.equals(displayName)) {
                    continue;
                }
            }
            double yPos = player.getPersistentData().getDouble(slotKey);
            Vec3 center = new Vec3(0.0, yPos, 0.0);
            for (Entity candidate : manipulationLevel.getEntitiesOfClass(
                Entity.class,
                Objects.requireNonNull(new AABB(center, center).inflate(0.5)),
                entity -> true
            )) {
                if (!(candidate instanceof LivingEntity living)) {
                    continue;
                }
                if (!storedName.equals(candidate.getName().getString())) {
                    continue;
                }
                if (friendNum != 0.0 && JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(candidate) != friendNum) {
                    continue;
                }
                return new ResolvedSummon(skillId, resolvedName, getPreviewBaseMaxHp(living), 1, Set.of(candidate.getType()), 40L);
            }
            break;
        }
        return null;
    }

    private static double getPreviewBaseMaxHp(LivingEntity livingEntity) {
        if (livingEntity.getPersistentData().contains(JJA_SUMMON_BASE_MAX_HEALTH, Tag.TAG_DOUBLE)) {
            return livingEntity.getPersistentData().getDouble(JJA_SUMMON_BASE_MAX_HEALTH);
        }
        return livingEntity.getMaxHealth();
    }

    private static ResolvedSummon single(
        int skillId,
        String resolvedName,
        double activationBaseMaxHp,
        EntityType<?> expectedEntityType,
        long pendingValidityTicks
    ) {
        return new ResolvedSummon(skillId, resolvedName, activationBaseMaxHp, 1, Set.of(expectedEntityType), pendingValidityTicks);
    }

    private static ResolvedSummon multi(
        int skillId,
        String resolvedName,
        double activationBaseMaxHp,
        int expectedCount,
        EntityType<?> expectedEntityType,
        long pendingValidityTicks
    ) {
        return new ResolvedSummon(skillId, resolvedName, activationBaseMaxHp, expectedCount, Set.of(expectedEntityType), pendingValidityTicks);
    }

    private static int getStrengthAmplifier(LivingEntity livingEntity) {
        MobEffect damageBoost = Objects.requireNonNull(MobEffects.DAMAGE_BOOST);
        if (livingEntity == null || !livingEntity.hasEffect(damageBoost)) {
            return 0;
        }
        net.minecraft.world.effect.MobEffectInstance effectInstance = livingEntity.getEffect(damageBoost);
        return effectInstance == null ? 0 : effectInstance.getAmplifier();
    }

    public record ResolvedSummon(
        int skillId,
        String resolvedTechniqueName,
        double activationBaseMaxHp,
        int expectedCount,
        Set<EntityType<?>> expectedEntityTypes,
        long pendingValidityTicks
    ) {
    }
}
