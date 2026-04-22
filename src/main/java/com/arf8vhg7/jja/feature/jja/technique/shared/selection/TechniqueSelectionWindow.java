package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

public final class TechniqueSelectionWindow {
    public static final double MIN_SELECT_ID = 0.0D;
    public static final double MAX_SELECT_ID = 21.0D;

    private TechniqueSelectionWindow() {
    }

    public static double advance(double select, boolean reverse) {
        double next = select + (reverse ? -1.0D : 1.0D);
        if (next < MIN_SELECT_ID) {
            return MAX_SELECT_ID;
        }
        if (next > MAX_SELECT_ID) {
            return MIN_SELECT_ID;
        }
        return next;
    }
}
