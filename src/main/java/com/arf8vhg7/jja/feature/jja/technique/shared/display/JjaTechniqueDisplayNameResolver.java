package com.arf8vhg7.jja.feature.jja.technique.shared.display;

import java.util.StringJoiner;
import net.minecraft.world.entity.Entity;

public final class JjaTechniqueDisplayNameResolver {
    private JjaTechniqueDisplayNameResolver() {
    }

    public static String resolveDisplayName(Entity entity, int curseTechniqueId, int selectTechniqueId, String canonicalName) {
        if (canonicalName == null || canonicalName.isEmpty()) {
            return "";
        }
        if (curseTechniqueId == 12) {
            if (selectTechniqueId == 5) {
                return JjaTechniqueNameKeyResolver.jjaTranslateTechniqueName("jujutsu.technique.granite_blast") + "1";
            }
            if (selectTechniqueId == 6) {
                return JjaTechniqueNameKeyResolver.jjaTranslateTechniqueName("jujutsu.technique.granite_blast") + "2";
            }
        }
        if (curseTechniqueId == 15) {
            if (selectTechniqueId == 8) {
                return translateJoined("jujutsu.technique.mahito_soul_multiplicity", "jujutsu.technique.mahito_body_repel1");
            }
            if (selectTechniqueId == 9) {
                return translateJoined("jujutsu.technique.mahito_soul_multiplicity", "jujutsu.technique.mahito_body_repel2");
            }
            if (selectTechniqueId == 10) {
                return translateJoined("jujutsu.technique.mahito_soul_multiplicity", "entity.jujutsucraft.polymorphic_soul_isomer");
            }
            if (selectTechniqueId == 15) {
                return translateJoined("jujutsu.technique.mahito_idel_mutation", "effect.instant_spirit_bodyof_distorted_killing_effect");
            }
        }
        return translateCanonicalName(canonicalName);
    }

    private static String translateJoined(String left, String right) {
        return JjaTechniqueNameKeyResolver.jjaTranslateTechniqueName(left)
            + ": "
            + JjaTechniqueNameKeyResolver.jjaTranslateTechniqueName(right);
    }

    private static String translateCanonicalName(String canonicalName) {
        if (!canonicalName.contains(": ")) {
            return JjaTechniqueNameKeyResolver.jjaTranslateTechniqueName(canonicalName);
        }
        StringJoiner joiner = new StringJoiner(": ");
        for (String segment : canonicalName.split(": ")) {
            joiner.add(JjaTechniqueNameKeyResolver.jjaTranslateTechniqueName(segment));
        }
        return joiner.toString();
    }
}
