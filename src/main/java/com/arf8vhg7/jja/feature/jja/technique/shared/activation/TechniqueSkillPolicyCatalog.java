package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import java.util.Map;

public final class TechniqueSkillPolicyCatalog {
    private static final TechniqueSkillPolicy UNRESTRICTED = TechniqueSkillPolicy.unrestricted();
    private static final Map<Integer, TechniqueSkillPolicy> POLICIES = Map.ofEntries(
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 107),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 215),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 405),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 406),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 605),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 606),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 607),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 608),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 610),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 611),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 612),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 613),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 614),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 615),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 617),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 618),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 717),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 809),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 1005),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 1006),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 1007),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 1506),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 1508),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 1509),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2005),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2113),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2205),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2207),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2208),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2210),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2305),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2405),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2505),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2815),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 2906),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 3810),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 4005),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 4008),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 4610),
        restrictedEntry(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS, 4706),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 105),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 106),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 206),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 207),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 208),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 506),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 507),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 609),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 805),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 415),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 1505),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 1507),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 1608),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 2108),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 2406),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 2408),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 2409),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 4708),
        restrictedEntry(TechniqueHandRestriction.FORBID_ANY_HELD_ITEM, 4709),
        disableOnlyEntry(703),
        disableOnlyEntry(719),
        disableOnlyEntry(1607),
        disableOnlyEntry(1706),
        disableOnlyEntry(2006),
        disableOnlyEntry(2015),
        disableOnlyEntry(2904),
        disableOnlyEntry(4705)
    );

    private TechniqueSkillPolicyCatalog() {
    }

    public static TechniqueSkillPolicy resolve(int skillId) {
        TechniqueSkillPolicy explicit = POLICIES.get(skillId);
        if (explicit != null) {
            return explicit;
        }
        if (isDefaultRestrictedDomain(skillId)) {
            return TechniqueSkillPolicy.restricted(TechniqueHandRestriction.FORBID_BOTH_HELD_ITEMS);
        }
        return UNRESTRICTED;
    }

    private static boolean isDefaultRestrictedDomain(int skillId) {
        return skillId > 0 && skillId % 100 == 20 && skillId != 1520;
    }

    private static Map.Entry<Integer, TechniqueSkillPolicy> restrictedEntry(TechniqueHandRestriction restriction, int skillId) {
        return Map.entry(skillId, TechniqueSkillPolicy.restricted(restriction));
    }

    private static Map.Entry<Integer, TechniqueSkillPolicy> disableOnlyEntry(int skillId) {
        return Map.entry(skillId, new TechniqueSkillPolicy(TechniqueHandRestriction.NONE, true));
    }
}
