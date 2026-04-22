package com.arf8vhg7.jja.feature.player.progression.physicalgifted;

import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeTier;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class MasterPhysicalGiftedGrantService {
    private MasterPhysicalGiftedGrantService() {
    }

    public static boolean shouldGrant(
        SorcererGradeTier highestTier,
        double playerCurseTechnique,
        double playerCurseTechnique2,
        double playerCursePower,
        boolean alreadyHasPhysicalGifted
    ) {
        if (highestTier == null || alreadyHasPhysicalGifted) {
            return false;
        }
        if (highestTier.rank() < SorcererGradeTier.SPECIAL.rank()) {
            return false;
        }
        return (playerCurseTechnique == -1.0 || playerCurseTechnique2 == -1.0) && playerCursePower > 0.0;
    }

    public static void grantIfEligible(Player player, JujutsucraftModVariables.PlayerVariables playerVariables) {
        if (player == null || playerVariables == null) {
            return;
        }
        ItemStack physicalGifted = new ItemStack(JujutsucraftModItems.ITEM_MASTER_PHYSICAL_GIFTED.get());
        if (!player.getInventory().contains(physicalGifted)
            && (playerVariables.PlayerCurseTechnique == -1.0 || playerVariables.PlayerCurseTechnique2 == -1.0)
            && playerVariables.PlayerCursePower > 0.0) {
            ItemHandlerHelper.giveItemToPlayer(player, physicalGifted);
        }
    }
}
