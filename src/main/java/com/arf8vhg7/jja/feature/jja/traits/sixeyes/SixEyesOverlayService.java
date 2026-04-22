package com.arf8vhg7.jja.feature.jja.traits.sixeyes;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.mcreator.jujutsucraft.entity.DivineDogTotalityEntity;
import net.mcreator.jujutsucraft.entity.MergedBeastAgitoEntity;
import net.mcreator.jujutsucraft.entity.SukunaEntity;
import net.mcreator.jujutsucraft.entity.SukunaFushiguroEntity;
import net.mcreator.jujutsucraft.entity.SukunaPerfectEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

public final class SixEyesOverlayService {
    private static final double TARGET_SCAN_DISTANCE = 16.0D;
    private static final double TARGET_SEARCH_RADIUS = 2.0D;
    private static final int CUSTOM_OVERLAY_FADE_TICKS = 14;
    private static final int MEGUMI_CT_ID = 6;

    private static SixEyesOverlaySnapshot currentSnapshot;
    private static int fadeTicksRemaining;

    private SixEyesOverlayService() {
    }

    public static void tickCustomOverlay(Player player) {
        if (player == null || player.level() == null || !player.isAlive()) {
            clearCustomOverlay();
            return;
        }

        if (!player.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.SIX_EYES.get()))) {
            clearCustomOverlay();
            return;
        }

        Optional<LivingEntity> resolvedTarget = resolveTarget(player);
        if (resolvedTarget.isPresent()) {
            currentSnapshot = buildSnapshot(resolvedTarget.get(), CUSTOM_OVERLAY_FADE_TICKS);
            fadeTicksRemaining = CUSTOM_OVERLAY_FADE_TICKS;
            return;
        }

        if (currentSnapshot == null) {
            fadeTicksRemaining = 0;
            return;
        }

        fadeTicksRemaining = Math.max(0, fadeTicksRemaining - 1);
        if (fadeTicksRemaining == 0) {
            clearCustomOverlay();
            return;
        }
        currentSnapshot = currentSnapshot.withFadeTicksRemaining(fadeTicksRemaining);
    }

    public static void clearCustomOverlay() {
        currentSnapshot = null;
        fadeTicksRemaining = 0;
    }

    public static boolean hasCustomOverlay() {
        return currentSnapshot != null && fadeTicksRemaining > 0;
    }

    public static SixEyesOverlaySnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }

    private static Optional<LivingEntity> resolveTarget(Player player) {
        Level level = player.level();
        double eyeX = player.getX();
        double eyeY = player.getEyeY();
        double eyeZ = player.getZ();
        double[] lookVector = resolveLookVector(player);
        double endX = eyeX + lookVector[0] * TARGET_SCAN_DISTANCE;
        double endY = eyeY + lookVector[1] * TARGET_SCAN_DISTANCE;
        double endZ = eyeZ + lookVector[2] * TARGET_SCAN_DISTANCE;
        AABB searchBox = new AABB(
            Math.min(eyeX, endX) - TARGET_SEARCH_RADIUS,
            Math.min(eyeY, endY) - TARGET_SEARCH_RADIUS,
            Math.min(eyeZ, endZ) - TARGET_SEARCH_RADIUS,
            Math.max(eyeX, endX) + TARGET_SEARCH_RADIUS,
            Math.max(eyeY, endY) + TARGET_SEARCH_RADIUS,
            Math.max(eyeZ, endZ) + TARGET_SEARCH_RADIUS
        );

        LivingEntity bestTarget = null;
        double bestDistanceSqr = Double.POSITIVE_INFINITY;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, searchBox, target -> isEligibleTarget(player, target))) {
            double hitDistanceSqr = resolveHitDistanceSqr(candidate, eyeX, eyeY, eyeZ, endX, endY, endZ);
            if (!Double.isFinite(hitDistanceSqr) || hitDistanceSqr >= bestDistanceSqr) {
                continue;
            }
            bestTarget = candidate;
            bestDistanceSqr = hitDistanceSqr;
        }
        return Optional.ofNullable(bestTarget);
    }

    private static double[] resolveLookVector(Player player) {
        float yawRadians = -player.getYRot() * ((float)Math.PI / 180F) - (float)Math.PI;
        float pitchRadians = -player.getXRot() * ((float)Math.PI / 180F);
        double cosPitch = -Mth.cos(pitchRadians);
        double sinPitch = Mth.sin(pitchRadians);
        return new double[] {
            Mth.sin(yawRadians) * cosPitch,
            sinPitch,
            Mth.cos(yawRadians) * cosPitch
        };
    }

    private static double resolveHitDistanceSqr(Entity candidate, double eyeX, double eyeY, double eyeZ, double endX, double endY, double endZ) {
        AABB hitBox = candidate.getBoundingBox().inflate(candidate.getPickRadius());
        if (contains(hitBox, eyeX, eyeY, eyeZ)) {
            return 0.0D;
        }
        return intersectSegment(hitBox, eyeX, eyeY, eyeZ, endX, endY, endZ);
    }

    private static boolean contains(AABB box, double x, double y, double z) {
        return x >= box.minX && x <= box.maxX && y >= box.minY && y <= box.maxY && z >= box.minZ && z <= box.maxZ;
    }

    private static double intersectSegment(AABB box, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        double deltaX = endX - startX;
        double deltaY = endY - startY;
        double deltaZ = endZ - startZ;

        AxisClipResult clipX = clipAxis(box.minX, box.maxX, startX, deltaX, 0.0D, 1.0D);
        if (!clipX.hit()) {
            return Double.POSITIVE_INFINITY;
        }

        AxisClipResult clipY = clipAxis(box.minY, box.maxY, startY, deltaY, clipX.min(), clipX.max());
        if (!clipY.hit()) {
            return Double.POSITIVE_INFINITY;
        }

        AxisClipResult clipZ = clipAxis(box.minZ, box.maxZ, startZ, deltaZ, clipY.min(), clipY.max());
        if (!clipZ.hit()) {
            return Double.POSITIVE_INFINITY;
        }

        double tMin = clipZ.min();
        if (tMin < 0.0D || tMin > 1.0D) {
            return Double.POSITIVE_INFINITY;
        }

        double hitX = startX + deltaX * tMin;
        double hitY = startY + deltaY * tMin;
        double hitZ = startZ + deltaZ * tMin;
        double diffX = hitX - startX;
        double diffY = hitY - startY;
        double diffZ = hitZ - startZ;
        return diffX * diffX + diffY * diffY + diffZ * diffZ;
    }

    private static AxisClipResult clipAxis(double min, double max, double start, double delta, double previousMin, double previousMax) {
        double newMin = previousMin;
        double newMax = previousMax;
        if (Math.abs(delta) < 1.0E-7D) {
            if (start < min || start > max) {
                return AxisClipResult.miss();
            }
        } else {
            double inverseDelta = 1.0D / delta;
            double t1 = (min - start) * inverseDelta;
            double t2 = (max - start) * inverseDelta;
            if (t1 > t2) {
                double swap = t1;
                t1 = t2;
                t2 = swap;
            }
            newMin = Math.max(newMin, t1);
            newMax = Math.min(newMax, t2);
            if (newMax < newMin) {
                return AxisClipResult.miss();
            }
        }
        return new AxisClipResult(true, newMin, newMax);
    }

    private record AxisClipResult(boolean hit, double min, double max) {
        static AxisClipResult miss() {
            return new AxisClipResult(false, 0.0D, 0.0D);
        }
    }

    private static boolean isEligibleTarget(Player player, LivingEntity target) {
        return target != player && isOwnedAutonomousSummon(player, target);
    }

    private static boolean isOwnedAutonomousSummon(Player player, LivingEntity target) {
        if (!SixEyesSummonTargetRules.isOwnedSummon(player, target)) {
            return false;
        }
        return JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player) != MEGUMI_CT_ID
            || target.getPersistentData().getBoolean("Ambush");
    }

    private static SixEyesOverlaySnapshot buildSnapshot(LivingEntity target, int fadeTicks) {
        List<SixEyesOverlayLine> lines = new ArrayList<>();
        addLine(lines, "screen.jja.sixeyes.health", resolveHealthValue(target), resolveHealthColor(target));
        addLine(lines, "screen.jja.sixeyes.main_hand", resolveItemName(target.getMainHandItem()), 0xE5E7EB);
        addLine(lines, "screen.jja.sixeyes.off_hand", resolveItemName(target.getOffhandItem()), 0xE5E7EB);
        addLine(lines, "screen.jja.sixeyes.armor.helmet", resolveItemName(target.getItemBySlot(EquipmentSlot.HEAD)), 0xE5E7EB);
        addLine(lines, "screen.jja.sixeyes.armor.chest", resolveItemName(target.getItemBySlot(EquipmentSlot.CHEST)), 0xE5E7EB);
        addLine(lines, "screen.jja.sixeyes.armor.legs", resolveItemName(target.getItemBySlot(EquipmentSlot.LEGS)), 0xE5E7EB);
        addLine(lines, "screen.jja.sixeyes.armor.feet", resolveItemName(target.getItemBySlot(EquipmentSlot.FEET)), 0xE5E7EB);

        String specialText = resolveSpecialText(target);
        if (!specialText.isEmpty()) {
            lines.add(new SixEyesOverlayLine(Component.translatable("screen.jja.sixeyes.special"), Component.literal(specialText), 0xFBBF24));
        }

        return new SixEyesOverlaySnapshot(
            Component.translatable("screen.jja.sixeyes.title"),
            target.getDisplayName(),
            List.copyOf(lines),
            fadeTicks,
            CUSTOM_OVERLAY_FADE_TICKS,
            resolveAccentColor(target)
        );
    }

    private static void addLine(List<SixEyesOverlayLine> lines, String labelKey, String value, int valueColor) {
        if (value.isEmpty()) {
            return;
        }
        lines.add(new SixEyesOverlayLine(Component.translatable(Objects.requireNonNull(labelKey)), Component.literal(Objects.requireNonNull(value)), valueColor));
    }

    private static String resolveHealthValue(LivingEntity target) {
        float currentHealth = FirstAidHealthAccess.getEffectiveHealth(target);
        return Math.round(currentHealth) + " / " + Math.round(target.getMaxHealth());
    }

    private static int resolveHealthColor(LivingEntity target) {
        float ratio = target.getMaxHealth() <= 0.0F ? 0.0F : FirstAidHealthAccess.getEffectiveHealth(target) / target.getMaxHealth();
        if (ratio <= 0.25F) {
            return 0xF87171;
        }
        if (ratio <= 0.5F) {
            return 0xFBBF24;
        }
        return 0x86EFAC;
    }

    private static String resolveItemName(ItemStack itemStack) {
        return itemStack.isEmpty() ? "" : itemStack.getHoverName().getString();
    }

    private static String resolveSpecialText(LivingEntity target) {
        if (target instanceof DivineDogTotalityEntity) {
            return Component.translatable("entity.jujutsucraft.divine_dog_white").getString()
                + ", "
                + Component.translatable("entity.jujutsucraft.divine_dog_black").getString();
        }
        if (target instanceof MergedBeastAgitoEntity) {
            return Component.translatable("entity.jujutsucraft.nue").getString()
                + ", "
                + Component.translatable("entity.jujutsucraft.great_serpent").getString()
                + ", "
                + Component.translatable("entity.jujutsucraft.round_deer").getString()
                + ", "
                + Component.translatable("entity.jujutsucraft.tiger_funeral").getString();
        }
        if (target instanceof SukunaEntity) {
            return Component.translatable("gui.jujutsucraft.select_technique.button_itadori1").getString();
        }
        if (target instanceof SukunaFushiguroEntity || target instanceof SukunaPerfectEntity) {
            return Component.translatable("gui.jujutsucraft.select_technique.button_megumi_fushiguro1").getString();
        }
        return "";
    }

    private static int resolveAccentColor(LivingEntity target) {
        if (target instanceof SukunaEntity || target instanceof SukunaFushiguroEntity || target instanceof SukunaPerfectEntity) {
            return 0xF97316;
        }
        if (target instanceof DivineDogTotalityEntity || target instanceof MergedBeastAgitoEntity) {
            return 0x60A5FA;
        }
        return 0x5EEAD4;
    }
}
