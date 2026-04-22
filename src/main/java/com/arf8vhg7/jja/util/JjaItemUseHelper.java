package com.arf8vhg7.jja.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class JjaItemUseHelper {
    private JjaItemUseHelper() {
    }

    public static boolean confirmConsumableUse(LevelAccessor world, Entity entity, boolean shouldConsume) {
        double batY = spawnMarkerBat(world, entity, shouldConsume);
        return shouldConsume && consumeSpawnedBat(world, entity, batY);
    }

    public static void applyCooldown(Entity entity, ItemStack itemStack, int ticks) {
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(itemStack.getItem(), ticks);
        }
    }

    public static void displayDontUse(Entity entity) {
        displayDontUse(entity, false);
    }

    public static void displayDontUse(Entity entity, boolean actionBar) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("jujutsu.message.dont_use"), actionBar);
        }
    }

    private static double spawnMarkerBat(LevelAccessor world, Entity entity, boolean shouldSpawn) {
        double batY = -200.0 - Math.random() * 20.0;
        if (!shouldSpawn || !(world instanceof ServerLevel serverLevel)) {
            return batY;
        }
        Bat bat = EntityType.BAT.create(serverLevel);
        if (bat == null) {
            return batY;
        }
        bat.moveTo(entity.getX(), batY, entity.getZ(), world.getRandom().nextFloat() * 360.0F, 0.0F);
        serverLevel.addFreshEntity(bat);
        return batY;
    }

    private static boolean consumeSpawnedBat(LevelAccessor world, Entity entity, double batY) {
        if (!(world instanceof Level level)) {
            return false;
        }
        Vec3 center = new Vec3(entity.getX(), batY, entity.getZ());
        for (Entity marker : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(0.5), candidate -> true)) {
            if (marker instanceof Bat bat && bat.isAlive()) {
                bat.discard();
                return true;
            }
        }
        return false;
    }
}
