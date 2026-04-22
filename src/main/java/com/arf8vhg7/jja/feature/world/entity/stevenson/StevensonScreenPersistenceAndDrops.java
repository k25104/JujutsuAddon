package com.arf8vhg7.jja.feature.world.entity.stevenson;

import com.arf8vhg7.jja.JujutsuAddon;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.mcreator.jujutsucraft.procedures.CursedSpiritGrade37EntityDiesProcedure;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class StevensonScreenPersistenceAndDrops {
    private StevensonScreenPersistenceAndDrops() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }
        if (mob.getType() != JujutsucraftModEntities.STEVENSON_SCREEN.get()) {
            return;
        }

        mob.setPersistenceRequired();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity().getType() != JujutsucraftModEntities.STEVENSON_SCREEN.get()) {
            return;
        }

        event.getDrops().clear();
        CursedSpiritGrade37EntityDiesProcedure.execute(
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ()
        );
    }
}
