package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public final class TechniqueSelectionUsageBalancer {
    private TechniqueSelectionUsageBalancer() {
    }

    public static Map<Integer, Integer> createUsageCounts(Iterable<Integer> techniqueIds) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer techniqueId : techniqueIds) {
            if (techniqueId != null) {
                counts.put(techniqueId, 0);
            }
        }
        return counts;
    }

    public static void incrementIfPresent(Map<Integer, Integer> usageCounts, int techniqueId) {
        if (usageCounts.containsKey(techniqueId)) {
            usageCounts.put(techniqueId, usageCounts.get(techniqueId) + 1);
        }
    }

    public static <T> List<T> resolveLeastUsed(
        Map<Integer, Integer> usageCounts,
        Iterable<T> entries,
        ToIntFunction<T> techniqueIdResolver
    ) {
        List<T> candidates = new ArrayList<>();
        int minimumUsage = Integer.MAX_VALUE;
        for (T entry : entries) {
            int usage = usageCounts.getOrDefault(techniqueIdResolver.applyAsInt(entry), 0);
            if (usage < minimumUsage) {
                minimumUsage = usage;
                candidates.clear();
                candidates.add(entry);
            } else if (usage == minimumUsage) {
                candidates.add(entry);
            }
        }
        return candidates;
    }
}
