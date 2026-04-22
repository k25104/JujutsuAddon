package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.progression.fame.FameFeatureConfig;
import com.arf8vhg7.jja.feature.player.progression.fame.FameGainMessageService;
import com.arf8vhg7.jja.feature.player.progression.fame.FameGainPolicy;
import com.arf8vhg7.jja.feature.player.progression.fame.network.JjaFameGainClientMessage;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeAdvancementHelper;
import java.util.ArrayList;
import java.util.List;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public final class WhenEntityDieProcedureHook {
    private WhenEntityDieProcedureHook() {
    }

    public static LivingEntity resolveFameTarget(Mob mob) {
        return FameGainPolicy.resolveFameTarget(mob);
    }

    public static boolean allowFameWhenUntargeted() {
        return FameGainPolicy.allowFameWhenUntargeted();
    }

    public static boolean shouldBlockSukunaFame(boolean originalHasSukunaEffect) {
        return FameFeatureConfig.shouldBlockSukunaFame(originalHasSukunaEffect);
    }

    public static List<Entity> appendResolvedFameTargetRecipient(Entity defeatedEntity, List<Entity> recipients) {
        if (!(defeatedEntity instanceof Mob mob)) {
            return recipients;
        }
        LivingEntity resolvedTarget = resolveFameTarget(mob);
        if (!shouldAppendResolvedFameTarget(resolvedTarget instanceof Player, recipients.contains(resolvedTarget))) {
            return recipients;
        }
        List<Entity> expandedRecipients = new ArrayList<>(recipients);
        expandedRecipients.add(resolvedTarget);
        return expandedRecipients;
    }

    public static boolean sendFameGainChat(Player player, Entity defeatedEntity, double fame) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        JjaFameGainClientMessage message = FameGainMessageService.createClientMessage(defeatedEntity, player, fame);
        if (message == null) {
            return false;
        }
        JjaPacketSenders.sendToPlayer(serverPlayer, message);
        return true;
    }

    public static void syncSpecialGrades(LevelAccessor world) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        double difficulty = world.getLevelData().getGameRules().getInt(net.mcreator.jujutsucraft.init.JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY);
        for (ServerPlayer player : serverLevel.players()) {
            JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
            if (playerVars == null) {
                continue;
            }
            SorcererGradeAdvancementHelper.syncSpecialTierFromFame(player, playerVars.PlayerFame, difficulty);
        }
    }

    static boolean shouldAppendResolvedFameTarget(boolean resolvedTargetIsPlayer, boolean alreadyPresent) {
        return resolvedTargetIsPlayer && !alreadyPresent;
    }
}
