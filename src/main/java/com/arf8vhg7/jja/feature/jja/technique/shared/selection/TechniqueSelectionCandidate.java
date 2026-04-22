package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

public record TechniqueSelectionCandidate(double select, String name, boolean passive, boolean physical, double cost) {
    private static final String EMPTY_NAME = "-----";

    public static TechniqueSelectionCandidate none(double select) {
        return new TechniqueSelectionCandidate(select, EMPTY_NAME, false, false, 0.0D);
    }

    public static TechniqueSelectionCandidate attack(double select, int selectionId) {
        return new TechniqueSelectionCandidate(select, "jujutsu.technique.attack" + (selectionId + 1), false, true, 0.0D);
    }

    public static TechniqueSelectionCandidate cancelDomain(double select) {
        return new TechniqueSelectionCandidate(select, "jujutsu.technique.cancel_domain", true, true, 0.0D);
    }

    public boolean isEmpty() {
        return EMPTY_NAME.equals(this.name);
    }
}
