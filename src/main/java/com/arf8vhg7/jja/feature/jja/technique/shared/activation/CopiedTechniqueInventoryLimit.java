package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public final class CopiedTechniqueInventoryLimit {
    static final int MAX_COPIES_PER_SKILL = 10;

    private CopiedTechniqueInventoryLimit() {
    }

    public static boolean canGiveCopiedTechnique(@Nullable Player player, @Nullable ItemStack candidate) {
        CopiedTechniqueEntry candidateEntry = toCopiedTechniqueEntry(candidate);
        if (player == null || !candidateEntry.isLimitedCandidate()) {
            return true;
        }
        return player.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
            .resolve()
            .map(itemHandler -> countCopiedTechniqueCopies(itemHandler, candidateEntry.skillId()) + candidateEntry.count() <= MAX_COPIES_PER_SKILL)
            .orElse(true);
    }

    static boolean canGiveCopiedTechnique(Iterable<CopiedTechniqueEntry> inventory, CopiedTechniqueEntry candidate) {
        if (candidate == null || !candidate.isLimitedCandidate()) {
            return true;
        }
        return countCopiedTechniqueCopies(inventory, candidate.skillId()) + candidate.count() <= MAX_COPIES_PER_SKILL;
    }

    static int countCopiedTechniqueCopies(Iterable<CopiedTechniqueEntry> inventory, int skillId) {
        int total = 0;
        for (CopiedTechniqueEntry entry : inventory) {
            if (entry != null && entry.matchesSkill(skillId)) {
                total += entry.count();
            }
        }
        return total;
    }

    private static int countCopiedTechniqueCopies(IItemHandler itemHandler, int skillId) {
        int total = 0;
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            CopiedTechniqueEntry entry = toCopiedTechniqueEntry(itemHandler.getStackInSlot(slot));
            if (entry.matchesSkill(skillId)) {
                total += entry.count();
            }
        }
        return total;
    }

    private static boolean isCopiedTechniqueItem(ItemStack stack) {
        return stack.getItem() == JujutsucraftModItems.COPIED_CURSED_TECHNIQUE.get();
    }

    private static CopiedTechniqueEntry toCopiedTechniqueEntry(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return CopiedTechniqueEntry.EMPTY;
        }
        String ignoredState = stack.hasTag() ? String.valueOf(stack.getTag()) : "";
        return new CopiedTechniqueEntry(
            isCopiedTechniqueItem(stack),
            TechniqueSkillResolver.resolveCopiedSkillId(stack),
            stack.getCount(),
            ignoredState
        );
    }

    static record CopiedTechniqueEntry(boolean copiedTechnique, int skillId, int count, String ignoredState) {
        private static final CopiedTechniqueEntry EMPTY = new CopiedTechniqueEntry(false, 0, 0, "");

        boolean isLimitedCandidate() {
            return this.copiedTechnique && this.skillId > 0 && this.count > 0;
        }

        boolean matchesSkill(int expectedSkillId) {
            return this.copiedTechnique && this.skillId == expectedSkillId && this.count > 0;
        }
    }
}
