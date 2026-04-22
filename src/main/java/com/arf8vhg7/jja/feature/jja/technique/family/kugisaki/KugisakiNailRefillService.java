package com.arf8vhg7.jja.feature.jja.technique.family.kugisaki;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemHandlerHelper;

public final class KugisakiNailRefillService {
    public static final int NAIL_REFILL_COUNT = 32;

    private KugisakiNailRefillService() {
    }

    public static boolean tryHandle(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return false;
        }

        NailRefillResolution resolution = resolveActivation(JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(entity));
        if (!resolution.handled()) {
            return false;
        }
        if (entity.level().isClientSide()) {
            return true;
        }

        if (entity instanceof Player player && resolution.grantedNails() > 0) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(JujutsucraftModItems.NAIL.get(), resolution.grantedNails()));
        }

        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(entity, resolution.nextSkill());
        if (resolution.removeTechniqueEffect() && entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get());
        }
        return true;
    }

    static NailRefillResolution resolveActivation(double currentSkill) {
        if ((int) Math.round(currentSkill) != KugisakiTechniqueSelectionService.NAIL_REFILL_SKILL) {
            return NailRefillResolution.none();
        }
        return new NailRefillResolution(true, NAIL_REFILL_COUNT, 0.0D, true);
    }

    record NailRefillResolution(boolean handled, int grantedNails, double nextSkill, boolean removeTechniqueEffect) {
        static NailRefillResolution none() {
            return new NailRefillResolution(false, 0, 0.0D, false);
        }
    }
}
