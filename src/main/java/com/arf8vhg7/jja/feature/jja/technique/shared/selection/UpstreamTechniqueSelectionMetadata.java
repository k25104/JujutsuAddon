package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public final class UpstreamTechniqueSelectionMetadata {
    private static final String RESOURCE_PATH = "jja/upstream-technique-selection.properties";
    private static final UpstreamTechniqueSelectionMetadata INSTANCE = load();

    private final String upstreamVersion;
    private final List<SelectableTechnique> randomSelectableTechniques;
    private final List<Integer> pageUpperBounds;
    private final Set<Integer> directSelectionTechniqueIds;
    private final int maxSelectableTechniqueId;

    private UpstreamTechniqueSelectionMetadata(
        String upstreamVersion,
        List<SelectableTechnique> randomSelectableTechniques,
        List<Integer> pageUpperBounds,
        Set<Integer> directSelectionTechniqueIds,
        int maxSelectableTechniqueId
    ) {
        this.upstreamVersion = upstreamVersion;
        this.randomSelectableTechniques = List.copyOf(randomSelectableTechniques);
        this.pageUpperBounds = List.copyOf(pageUpperBounds);
        this.directSelectionTechniqueIds = Collections.unmodifiableSet(new LinkedHashSet<>(directSelectionTechniqueIds));
        this.maxSelectableTechniqueId = maxSelectableTechniqueId;
    }

    public static UpstreamTechniqueSelectionMetadata get() {
        return INSTANCE;
    }

    public String upstreamVersion() {
        return this.upstreamVersion;
    }

    public List<SelectableTechnique> randomSelectableTechniques() {
        return this.randomSelectableTechniques;
    }

    public List<Integer> pageUpperBounds() {
        return this.pageUpperBounds;
    }

    public Set<Integer> directSelectionTechniqueIds() {
        return this.directSelectionTechniqueIds;
    }

    public int maxSelectableTechniqueId() {
        return this.maxSelectableTechniqueId;
    }

    public int maxCurseTechniqueId() {
        return this.pageUpperBounds.isEmpty() ? 0 : this.pageUpperBounds.get(this.pageUpperBounds.size() - 1);
    }

    public boolean usesDirectSelectionWindow(int curseTechniqueId) {
        return curseTechniqueId <= firstPageUpperBound() || this.directSelectionTechniqueIds.contains(curseTechniqueId);
    }

    public int resolvePageNumber(int curseTechniqueId) {
        if (usesDirectSelectionWindow(curseTechniqueId)) {
            return 1;
        }

        for (int index = 1; index < this.pageUpperBounds.size(); index++) {
            if (curseTechniqueId <= this.pageUpperBounds.get(index)) {
                return index + 1;
            }
        }
        return this.pageUpperBounds.isEmpty() ? 1 : this.pageUpperBounds.size();
    }

    private int firstPageUpperBound() {
        return this.pageUpperBounds.isEmpty() ? Integer.MAX_VALUE : this.pageUpperBounds.get(0);
    }

    private static UpstreamTechniqueSelectionMetadata load() {
        Properties properties = new Properties();
        try (InputStream input = UpstreamTechniqueSelectionMetadata.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
            if (input == null) {
                throw new IllegalStateException("Missing generated metadata resource '" + RESOURCE_PATH + "'. Run ./gradlew build.");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to load generated metadata resource '" + RESOURCE_PATH + "'", exception);
        }

        List<SelectableTechnique> randomSelectableTechniques = parseSelectableTechniques(properties);
        List<Integer> pageUpperBounds = parseIntegerList(properties, "pageUpperBounds");
        validatePageUpperBounds(pageUpperBounds);
        Set<Integer> directSelectionTechniqueIds = parseIntegerSet(properties, "directSelectionTechniqueIds");
        int maxSelectableTechniqueId = parseIntegerProperty(properties, "maxSelectableTechniqueId");
        return new UpstreamTechniqueSelectionMetadata(
            requireProperty(properties, "version"),
            randomSelectableTechniques,
            pageUpperBounds,
            directSelectionTechniqueIds,
            maxSelectableTechniqueId
        );
    }

    private static List<SelectableTechnique> parseSelectableTechniques(Properties properties) {
        String rawEntries = requireProperty(properties, "randomSelectableEntries");
        List<SelectableTechnique> techniques = new ArrayList<>();
        if (rawEntries.isEmpty()) {
            return techniques;
        }

        for (String rawEntry : rawEntries.split(",")) {
            String entry = rawEntry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            String[] parts = entry.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalStateException("Malformed randomSelectableEntries row: " + entry);
            }
            techniques.add(new SelectableTechnique(parts[0], parseInteger(parts[1], "randomSelectableEntries")));
        }
        return techniques;
    }

    private static List<Integer> parseIntegerList(Properties properties, String key) {
        String rawValue = requireProperty(properties, key);
        List<Integer> values = new ArrayList<>();
        if (rawValue.isEmpty()) {
            return values;
        }

        for (String token : rawValue.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                values.add(parseInteger(trimmed, key));
            }
        }
        return values;
    }

    private static Set<Integer> parseIntegerSet(Properties properties, String key) {
        return new LinkedHashSet<>(parseIntegerList(properties, key));
    }

    private static int parseIntegerProperty(Properties properties, String key) {
        return parseInteger(requireProperty(properties, key), key);
    }

    private static int parseInteger(String rawValue, String key) {
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Malformed integer for " + key + ": " + rawValue, exception);
        }
    }

    private static String requireProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing generated metadata property '" + key + "'");
        }
        return value.trim();
    }

    private static void validatePageUpperBounds(List<Integer> pageUpperBounds) {
        for (int index = 1; index < pageUpperBounds.size(); index++) {
            if (pageUpperBounds.get(index - 1) >= pageUpperBounds.get(index)) {
                throw new IllegalStateException("Generated pageUpperBounds must be strictly increasing: " + pageUpperBounds);
            }
        }
    }

    public record SelectableTechnique(String procedureName, int techniqueId) {
        public SelectableTechnique {
            Objects.requireNonNull(procedureName, "procedureName");
            if (procedureName.isBlank()) {
                throw new IllegalArgumentException("procedureName must not be blank");
            }
        }
    }
}
