package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import java.util.function.DoubleFunction;
import java.util.function.Predicate;

public final class TechniqueSelectionCycle {
    private TechniqueSelectionCycle() {
    }

    public static <T> T resolve(
        double initialSelect,
        boolean reverse,
        int maxSelectionSteps,
        DoubleFunction<T> resolver,
        Predicate<T> isEmpty,
        Predicate<T> isSelectable,
        DoubleFunction<T> emptyFactory
    ) {
        double select = initialSelect;
        for (int index = 0; index < maxSelectionSteps; index++) {
            T candidate = resolver.apply(select);
            if (!isEmpty.test(candidate) && isSelectable.test(candidate)) {
                return candidate;
            }
            select = TechniqueSelectionWindow.advance(select, reverse);
        }
        return emptyFactory.apply(select);
    }
}
