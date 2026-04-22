package com.arf8vhg7.jja.feature.jja.technique.shared.registration;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaAddonTechniqueSelectionCatalog;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.UpstreamTechniqueSelectionMetadata;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaSkillManagementPageDispatcher;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaSkillManagementProbeContext;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueDisplayNameResolver;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.ShikigamiTechniqueRegistrationDisplayService;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;

public final class JjaSkillManagementProbe {
    private static final int MIN_SELECT_TECHNIQUE_ID = 0;
    private static final UpstreamTechniqueSelectionMetadata TECHNIQUE_SELECTION_METADATA = UpstreamTechniqueSelectionMetadata.get();
    private static final int MAX_SELECT_TECHNIQUE_ID = TECHNIQUE_SELECTION_METADATA.maxSelectableTechniqueId();

    private JjaSkillManagementProbe() {
    }

    public static int maxSelectableTechniqueId() {
        return MAX_SELECT_TECHNIQUE_ID;
    }

    public static List<DisplaySkillCandidate> collectCandidates(ServerPlayer player) {
        if (player == null) {
            return List.of();
        }
        int curseTechniqueId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player);
        if (curseTechniqueId < -1 || curseTechniqueId == 0 || curseTechniqueId > TECHNIQUE_SELECTION_METADATA.maxCurseTechniqueId()) {
            return List.of();
        }
        Map<String, Set<String>> candidateMap = new LinkedHashMap<>();
        for (int selectTechniqueId = MIN_SELECT_TECHNIQUE_ID; selectTechniqueId <= MAX_SELECT_TECHNIQUE_ID; selectTechniqueId++) {
            ProbeSkillCandidate candidate = probeCandidate(player, curseTechniqueId, selectTechniqueId);
            if (candidate == null) {
                continue;
            }
            candidateMap.computeIfAbsent(candidate.displayName(), key -> new LinkedHashSet<>()).add(candidate.canonicalName());
        }
        List<DisplaySkillCandidate> displayCandidates = new ArrayList<>(candidateMap.size());
        for (Map.Entry<String, Set<String>> entry : candidateMap.entrySet()) {
            displayCandidates.add(new DisplaySkillCandidate(entry.getKey(), List.copyOf(entry.getValue())));
        }
        return displayCandidates;
    }

    public static List<TechniqueSetupRegistrationCandidate> collectRegistrationCandidates(ServerPlayer player) {
        if (player == null) {
            return List.of();
        }
        int curseTechniqueId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player);
        if (curseTechniqueId < -1 || curseTechniqueId == 0 || curseTechniqueId > TECHNIQUE_SELECTION_METADATA.maxCurseTechniqueId()) {
            return List.of();
        }
        List<TechniqueSetupRegistrationCandidate> candidates = new ArrayList<>();
        for (int selectTechniqueId = MIN_SELECT_TECHNIQUE_ID; selectTechniqueId <= MAX_SELECT_TECHNIQUE_ID; selectTechniqueId++) {
            ProbeSkillCandidate candidate = probeCandidate(player, curseTechniqueId, selectTechniqueId);
            if (candidate == null) {
                continue;
            }
            candidates.add(new TechniqueSetupRegistrationCandidate(
                formatRegistrationDisplayName(player, curseTechniqueId, candidate),
                candidate.selectTechniqueId(),
                candidate.canonicalName()
            ));
        }
        return candidates;
    }

    private static ProbeSkillCandidate probeCandidate(ServerPlayer player, int curseTechniqueId, int initialSelectTechniqueId) {
        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVars == null) {
            return null;
        }
        double originalSelectTechnique = playerVars.PlayerSelectCurseTechnique;
        double originalSelectTechniqueCost = playerVars.PlayerSelectCurseTechniqueCost;
        double originalSelectTechniqueCostOrigin = playerVars.PlayerSelectCurseTechniqueCostOrgin;
        boolean originalNoChangeTechnique = playerVars.noChangeTechnique;
        JjaSkillManagementProbeContext.ProbeSnapshot snapshot;
        try {
            if (JjaSkillManagementPageDispatcher.usesDirectSelectionWindow(curseTechniqueId)) {
                playerVars.PlayerSelectCurseTechnique = initialSelectTechniqueId;
                playerVars.noChangeTechnique = true;
            }
            snapshot = JjaSkillManagementProbeContext.runProbe(
                initialSelectTechniqueId,
                true,
                () -> JjaSkillManagementPageDispatcher.invoke(player, curseTechniqueId, initialSelectTechniqueId)
            );
        } finally {
            playerVars.PlayerSelectCurseTechnique = originalSelectTechnique;
            playerVars.PlayerSelectCurseTechniqueCost = originalSelectTechniqueCost;
            playerVars.PlayerSelectCurseTechniqueCostOrgin = originalSelectTechniqueCostOrigin;
            playerVars.noChangeTechnique = originalNoChangeTechnique;
        }
        if (!snapshot.matchesInitialSelect() || snapshot.skipped()) {
            return resolveAddonProbeCandidate(player, playerVars, curseTechniqueId, initialSelectTechniqueId);
        }
        String canonicalName = snapshot.canonicalName();
        if (canonicalName == null || canonicalName.isEmpty() || "-----".equals(canonicalName)) {
            return resolveAddonProbeCandidate(player, playerVars, curseTechniqueId, initialSelectTechniqueId);
        }
        return buildProbeCandidate(
            player,
            snapshot.curseTechniqueId(),
            snapshot.selectTechniqueId(),
            canonicalName,
            snapshot.cost(),
            playerVars
        );
    }

    private static ProbeSkillCandidate resolveAddonProbeCandidate(
        ServerPlayer player,
        JujutsucraftModVariables.PlayerVariables playerVars,
        int curseTechniqueId,
        int selectTechniqueId
    ) {
        TechniqueSelectionCandidate candidate = JjaAddonTechniqueSelectionCatalog.resolveCandidate(curseTechniqueId, selectTechniqueId);
        if (candidate.isEmpty()) {
            return null;
        }
        return buildProbeCandidate(
            player,
            curseTechniqueId,
            (int) Math.round(candidate.select()),
            candidate.name(),
            candidate.cost(),
            playerVars
        );
    }

    private static ProbeSkillCandidate buildProbeCandidate(
        ServerPlayer player,
        int curseTechniqueId,
        int selectTechniqueId,
        String canonicalName,
        double cost,
        JujutsucraftModVariables.PlayerVariables playerVars
    ) {
        String displayName = JjaTechniqueDisplayNameResolver.resolveDisplayName(
            player,
            curseTechniqueId,
            selectTechniqueId,
            canonicalName
        );
        if (displayName == null || displayName.isEmpty() || "-----".equals(displayName)) {
            return null;
        }
        double displayedCost = SummonEnhancementService.resolveDisplayedTechniqueCost(
            player,
            playerVars,
            cost,
            curseTechniqueId,
            selectTechniqueId,
            canonicalName
        );
        return new ProbeSkillCandidate(canonicalName, displayName, selectTechniqueId, displayedCost);
    }

    private static String formatRegistrationDisplayName(ServerPlayer player, int curseTechniqueId, ProbeSkillCandidate candidate) {
        String displayName = formatRegistrationDisplayName(candidate.displayName(), candidate.cost());
        return ShikigamiTechniqueRegistrationDisplayService.appendSummonCountSuffix(
            player,
            curseTechniqueId,
            candidate.selectTechniqueId(),
            candidate.canonicalName(),
            displayName
        );
    }

    private static String formatRegistrationDisplayName(String displayName, double cost) {
        long formattedCost = Math.max(0L, Math.round(cost));
        if (formattedCost <= 0L) {
            return displayName;
        }
        return displayName + "(" + formattedCost + ")";
    }

    public record DisplaySkillCandidate(String displayName, List<String> canonicalNames) {
    }

    private record ProbeSkillCandidate(String canonicalName, String displayName, int selectTechniqueId, double cost) {
    }
}
