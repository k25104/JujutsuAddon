package com.arf8vhg7.jja.feature.jja.technique.shared.menu;

public record TechniqueSetupAvailability(int visibleCategoriesMask, int antiDomainAvailableMask, int domainTypeAvailableMask) {
    public boolean isVisible(TechniqueSetupCategory category) {
        return category != null && (this.visibleCategoriesMask & category.mask()) != 0;
    }
}
