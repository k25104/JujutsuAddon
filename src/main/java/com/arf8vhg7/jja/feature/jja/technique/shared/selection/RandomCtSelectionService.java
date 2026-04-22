package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionPendingRegistry;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionUsageBalancer;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.UpstreamTechniqueSelectionMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.mcreator.jujutsucraft.procedures.SelectAngelProcedure;
import net.mcreator.jujutsucraft.procedures.SelectChojuroProcedure;
import net.mcreator.jujutsucraft.procedures.SelectChosoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectCrystalCurseProcedure;
import net.mcreator.jujutsucraft.procedures.SelectDagonProcedure;
import net.mcreator.jujutsucraft.procedures.SelectDhruvLakdawallaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectFushiguroProcedure;
import net.mcreator.jujutsucraft.procedures.SelectGetoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectGojoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectHakariProcedure;
import net.mcreator.jujutsucraft.procedures.SelectHanamiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectHazenokiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectHigurumaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectInoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectInumakiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectIshigoriProcedure;
import net.mcreator.jujutsucraft.procedures.SelectItadoriProcedure;
import net.mcreator.jujutsucraft.procedures.SelectJinichiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectJogoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectJunpeProcedure;
import net.mcreator.jujutsucraft.procedures.SelectKaoriProcedure;
import net.mcreator.jujutsucraft.procedures.SelectKashimoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectKugisakiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectKurourushiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectKusakabeProcedure;
import net.mcreator.jujutsucraft.procedures.SelectMakiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectMahitoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectMahoragaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectMeiMeiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectMiguelProcedure;
import net.mcreator.jujutsucraft.procedures.SelectNaoyaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectNanamiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectNishimiyaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectOgiProcedure;
import net.mcreator.jujutsucraft.procedures.SelectOkkotsuProcedure;
import net.mcreator.jujutsucraft.procedures.SelectRantaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectReggieStarProcedure;
import net.mcreator.jujutsucraft.procedures.SelectRozetsuProcedure;
import net.mcreator.jujutsucraft.procedures.SelectSmallpoxDeityProcedure;
import net.mcreator.jujutsucraft.procedures.SelectSukunaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectTakabaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectTakakoUroProcedure;
import net.mcreator.jujutsucraft.procedures.SelectTodoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectTsukumoProcedure;
import net.mcreator.jujutsucraft.procedures.SelectUraumeProcedure;
import net.mcreator.jujutsucraft.procedures.SelectYagaProcedure;
import net.mcreator.jujutsucraft.procedures.SelectYorozuProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;

public final class RandomCtSelectionService {
    private static final Map<String, RandomCtExecutor> RANDOM_CT_EXECUTORS = Map.ofEntries(
        Map.entry("SelectAngelProcedure", SelectAngelProcedure::execute),
        Map.entry("SelectChojuroProcedure", SelectChojuroProcedure::execute),
        Map.entry("SelectChosoProcedure", SelectChosoProcedure::execute),
        Map.entry("SelectCrystalCurseProcedure", SelectCrystalCurseProcedure::execute),
        Map.entry("SelectDagonProcedure", SelectDagonProcedure::execute),
        Map.entry("SelectDhruvLakdawallaProcedure", SelectDhruvLakdawallaProcedure::execute),
        Map.entry("SelectFushiguroProcedure", SelectFushiguroProcedure::execute),
        Map.entry("SelectGetoProcedure", SelectGetoProcedure::execute),
        Map.entry("SelectGojoProcedure", SelectGojoProcedure::execute),
        Map.entry("SelectHakariProcedure", SelectHakariProcedure::execute),
        Map.entry("SelectHanamiProcedure", SelectHanamiProcedure::execute),
        Map.entry("SelectHazenokiProcedure", SelectHazenokiProcedure::execute),
        Map.entry("SelectHigurumaProcedure", SelectHigurumaProcedure::execute),
        Map.entry("SelectInoProcedure", SelectInoProcedure::execute),
        Map.entry("SelectInumakiProcedure", SelectInumakiProcedure::execute),
        Map.entry("SelectIshigoriProcedure", SelectIshigoriProcedure::execute),
        Map.entry("SelectItadoriProcedure", SelectItadoriProcedure::execute),
        Map.entry("SelectJinichiProcedure", SelectJinichiProcedure::execute),
        Map.entry("SelectJogoProcedure", SelectJogoProcedure::execute),
        Map.entry("SelectJunpeProcedure", SelectJunpeProcedure::execute),
        Map.entry("SelectKaoriProcedure", SelectKaoriProcedure::execute),
        Map.entry("SelectKashimoProcedure", SelectKashimoProcedure::execute),
        Map.entry("SelectKugisakiProcedure", SelectKugisakiProcedure::execute),
        Map.entry("SelectKurourushiProcedure", SelectKurourushiProcedure::execute),
        Map.entry("SelectKusakabeProcedure", SelectKusakabeProcedure::execute),
        Map.entry("SelectMakiProcedure", SelectMakiProcedure::execute),
        Map.entry("SelectMahitoProcedure", SelectMahitoProcedure::execute),
        Map.entry("SelectMahoragaProcedure", SelectMahoragaProcedure::execute),
        Map.entry("SelectMeiMeiProcedure", SelectMeiMeiProcedure::execute),
        Map.entry("SelectMiguelProcedure", SelectMiguelProcedure::execute),
        Map.entry("SelectNaoyaProcedure", SelectNaoyaProcedure::execute),
        Map.entry("SelectNanamiProcedure", SelectNanamiProcedure::execute),
        Map.entry("SelectNishimiyaProcedure", SelectNishimiyaProcedure::execute),
        Map.entry("SelectOgiProcedure", SelectOgiProcedure::execute),
        Map.entry("SelectOkkotsuProcedure", SelectOkkotsuProcedure::execute),
        Map.entry("SelectRantaProcedure", SelectRantaProcedure::execute),
        Map.entry("SelectReggieStarProcedure", SelectReggieStarProcedure::execute),
        Map.entry("SelectRozetsuProcedure", SelectRozetsuProcedure::execute),
        Map.entry("SelectSmallpoxDeityProcedure", SelectSmallpoxDeityProcedure::execute),
        Map.entry("SelectSukunaProcedure", SelectSukunaProcedure::execute),
        Map.entry("SelectTakabaProcedure", SelectTakabaProcedure::execute),
        Map.entry("SelectTakakoUroProcedure", SelectTakakoUroProcedure::execute),
        Map.entry("SelectTodoProcedure", SelectTodoProcedure::execute),
        Map.entry("SelectTsukumoProcedure", SelectTsukumoProcedure::execute),
        Map.entry("SelectUraumeProcedure", SelectUraumeProcedure::execute),
        Map.entry("SelectYagaProcedure", SelectYagaProcedure::execute),
        Map.entry("SelectYorozuProcedure", SelectYorozuProcedure::execute)
    );
    private static final List<RandomCtEntry> RANDOM_SELECTIONS = buildRandomSelections();
    private static final TechniqueSelectionPendingRegistry PENDING_SELECTABLE_RANDOM = new TechniqueSelectionPendingRegistry();

    private RandomCtSelectionService() {
    }

    public static void beginSelectableRandomSelection(Entity entity) {
        PENDING_SELECTABLE_RANDOM.begin(entity);
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof ServerPlayer player) || RANDOM_SELECTIONS.isEmpty()) {
            return false;
        }

        List<RandomCtEntry> candidates = resolveLeastUsedSelectableEntries(player);
        if (candidates.isEmpty()) {
            return false;
        }
        RandomCtEntry selected = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        selected.executor().execute(world, x, y, z, entity);
        return true;
    }

    public static boolean isSelectableRandomSelectionPending(Entity entity) {
        return PENDING_SELECTABLE_RANDOM.isPending(entity);
    }

    public static void finishSelectableRandomSelection(Entity entity) {
        PENDING_SELECTABLE_RANDOM.finish(entity);
    }

    static boolean isSelectableTechniqueId(int techniqueId) {
        return findSelectableEntry(techniqueId) != null;
    }

    static RandomCtEntry findSelectableEntry(int techniqueId) {
        for (RandomCtEntry entry : RANDOM_SELECTIONS) {
            if (entry.techniqueId() == techniqueId) {
                return entry;
            }
        }
        return null;
    }

    static Map<Integer, Integer> buildTechniqueUsageCounts(Iterable<? extends ServerPlayer> players) {
        Map<Integer, Integer> counts = createTechniqueUsageCounts();
        for (ServerPlayer player : players) {
            JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
            if (playerVariables == null) {
                continue;
            }
            incrementUsageCount(counts, (int) Math.round(playerVariables.PlayerCurseTechnique));
            incrementUsageCount(counts, (int) Math.round(playerVariables.PlayerCurseTechnique2));
        }
        return counts;
    }

    static List<RandomCtEntry> resolveLeastUsedSelectableEntries(Map<Integer, Integer> usageCounts) {
        return TechniqueSelectionUsageBalancer.resolveLeastUsed(usageCounts, RANDOM_SELECTIONS, RandomCtEntry::techniqueId);
    }

    private static List<RandomCtEntry> resolveLeastUsedSelectableEntries(ServerPlayer player) {
        return resolveLeastUsedSelectableEntries(buildTechniqueUsageCounts(player.server.getPlayerList().getPlayers()));
    }

    static Map<Integer, Integer> createTechniqueUsageCounts() {
        List<Integer> techniqueIds = new ArrayList<>(RANDOM_SELECTIONS.size());
        for (RandomCtEntry entry : RANDOM_SELECTIONS) {
            techniqueIds.add(entry.techniqueId());
        }
        return TechniqueSelectionUsageBalancer.createUsageCounts(techniqueIds);
    }

    private static void incrementUsageCount(Map<Integer, Integer> usageCounts, int techniqueId) {
        TechniqueSelectionUsageBalancer.incrementIfPresent(usageCounts, techniqueId);
    }

    private static List<RandomCtEntry> buildRandomSelections() {
        List<RandomCtEntry> randomSelections = new ArrayList<>();
        for (UpstreamTechniqueSelectionMetadata.SelectableTechnique technique : UpstreamTechniqueSelectionMetadata.get().randomSelectableTechniques()) {
            RandomCtExecutor executor = RANDOM_CT_EXECUTORS.get(technique.procedureName());
            if (executor == null) {
                throw new IllegalStateException("Missing random CT executor for " + technique.procedureName());
            }
            randomSelections.add(entry(technique.procedureName(), technique.techniqueId(), executor));
        }
        return List.copyOf(randomSelections);
    }

    private static RandomCtEntry entry(String procedureName, int techniqueId, RandomCtExecutor executor) {
        return new RandomCtEntry(procedureName, techniqueId, executor);
    }

    @FunctionalInterface
    public interface RandomCtExecutor {
        void execute(LevelAccessor world, double x, double y, double z, Entity entity);
    }

    public record RandomCtEntry(String procedureName, int techniqueId, RandomCtExecutor executor) {
    }
}
