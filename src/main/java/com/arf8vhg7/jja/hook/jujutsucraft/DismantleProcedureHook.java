package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.CutTheWorldWitnessService;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.mcreator.jujutsucraft.entity.SukunaFushiguroEntity;
import net.mcreator.jujutsucraft.entity.SukunaPerfectEntity;

public final class DismantleProcedureHook {
    private static final ResourceLocation CUT_THE_WORLD_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "skill_dismantle_cut_the_world"
    );

    private DismantleProcedureHook() {
    }

    public static MutableComponent buildChantMessage(int chantStep) {
        return Component.translatable("chant.jujutsucraft.dismantle" + chantStep);
    }

    public static boolean isChantStepReady(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt5ChantStepReady(entity, original);
    }

    public static void observeCutTheWorld(Entity entity) {
        if (entity != null && shouldObserveCutTheWorld(entity.getPersistentData().getDouble("cnt1"), entity.getPersistentData().getDouble("cnt6"), canUseWorld(entity))) {
            CutTheWorldWitnessService.witness(entity);
        }
    }

    static boolean shouldObserveCutTheWorld(double cnt1, double cnt6, boolean canUseWorld) {
        return Double.compare(cnt1, 4.0D) == 0 && cnt6 >= 5.0D && canUseWorld;
    }

    static boolean canUseWorld(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            return JjaAdvancementHelper.has(player, CUT_THE_WORLD_ID);
        }
        if (entity instanceof SukunaPerfectEntity) {
            return true;
        }
        if (entity instanceof SukunaFushiguroEntity sukunaFushiguroEntity) {
            return Boolean.TRUE.equals(sukunaFushiguroEntity.getEntityData().get(SukunaFushiguroEntity.DATA_world_cut));
        }
        return false;
    }
}
