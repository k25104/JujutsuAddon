package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.level.ServerPlayer;

public final class ForceRandomProfessionFlagService {
    private static final double MAKI_CURSE_TECHNIQUE = -1.0;
    private static final double HAKARI_CURSE_TECHNIQUE = 29.0;
    private static final List<String> RANDOM_PROFESSION_FLAGS = List.of("JujutsuSorcerer", "CurseUser", "CursedSpirit");
    private static final List<String> NON_CURSED_SPIRIT_FLAGS = List.of("JujutsuSorcerer", "CurseUser");

    private ForceRandomProfessionFlagService() {
    }

    public static void handle(ServerPlayer player) {
        player.getPersistentData().putBoolean("JujutsuSorcerer", false);
        player.getPersistentData().putBoolean("CurseUser", false);
        player.getPersistentData().putBoolean("CursedSpirit", false);
        player.getPersistentData().putBoolean("NonSorcerer", false);

        List<String> professionFlags = resolveProfessionFlags(player);
        String selectedFlag = professionFlags.get(ThreadLocalRandom.current().nextInt(professionFlags.size()));
        player.getPersistentData().putBoolean(selectedFlag, true);
    }

    static List<String> resolveProfessionFlags(ServerPlayer player) {
        var playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        double curseTechnique = playerVariables != null ? playerVariables.PlayerCurseTechnique : Double.NaN;
        return shouldExcludeCursedSpirit(curseTechnique) ? NON_CURSED_SPIRIT_FLAGS : RANDOM_PROFESSION_FLAGS;
    }

    static boolean shouldExcludeCursedSpirit(double curseTechnique) {
        return Double.compare(curseTechnique, MAKI_CURSE_TECHNIQUE) == 0 || Double.compare(curseTechnique, HAKARI_CURSE_TECHNIQUE) == 0;
    }
}
