package com.arf8vhg7.jja.feature.jja.domain.de;

public final class OpenBarrierUnlockRules {
    private OpenBarrierUnlockRules() {
    }

    public static boolean hasAccess(boolean hasSukunaEffect, boolean hasOpenBarrierMastery) {
        return hasSukunaEffect || hasOpenBarrierMastery;
    }

    public static boolean shouldUseOpenBarrier(DomainTypeOption selectedDomainType, boolean hasAccess) {
        return hasAccess && selectedDomainType != null && selectedDomainType.isOpenBarrier();
    }

    public static boolean isDomainTypeVisible(boolean hasDomainExpansionMastery, boolean hasAccess) {
        return hasDomainExpansionMastery && hasAccess;
    }

    public static int resolveDomainTypeAvailableMask(boolean hasDomainExpansionMastery, boolean hasAccess) {
        if (!isDomainTypeVisible(hasDomainExpansionMastery, hasAccess)) {
            return 0;
        }
        return DomainTypeOption.BARRIER.mask() | DomainTypeOption.OPEN_BARRIER.mask();
    }
}
