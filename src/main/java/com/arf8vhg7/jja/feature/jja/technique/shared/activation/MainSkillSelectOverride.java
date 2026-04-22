package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots.SelectOverride;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots.SelectOverrideType;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.LocateRikaProcedure;
import net.mcreator.jujutsucraft.procedures.TechniqueDecideProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class MainSkillSelectOverride {
    private MainSkillSelectOverride() {
    }

    public static SelectOverride resolve(LevelAccessor world, Entity entity, double currentCt, double currentSelect) {
        if (entity == null) {
            return SelectOverride.none();
        }

        double select = currentSelect;
        boolean changed = false;

        SelectOverride slotOverride = RegisteredCurseTechniqueSlots.resolveSelectOverride(entity, currentCt);
        if (slotOverride.type() == SelectOverrideType.CANCEL) {
            return slotOverride;
        }
        if (slotOverride.type() == SelectOverrideType.APPLY) {
            select = slotOverride.select();
            syncAddonSlotSelection(entity, currentCt, select);
            changed = true;
        }

        if (currentCt == 5.0 && select == 10.0 && LocateRikaProcedure.execute(world, entity)) {
            select = 19.0;
            changed = true;
        }

        if (select == 20.0
            && entity instanceof LivingEntity living
            && living.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get())) {
            select = 21.0;
            changed = true;
        }

        return changed ? SelectOverride.apply(select) : SelectOverride.none();
    }

    private static void syncAddonSlotSelection(Entity entity, double currentCt, double select) {
        TechniqueSelectionCandidate candidate = RegisteredCurseTechniqueSlots.resolveAddonCandidateForSelection(currentCt, select);
        if (candidate.isEmpty()) {
            return;
        }
        TechniqueDecideProcedure.execute(
            entity,
            candidate.passive(),
            candidate.physical(),
            candidate.cost(),
            currentCt,
            candidate.select(),
            candidate.name()
        );
    }
}
