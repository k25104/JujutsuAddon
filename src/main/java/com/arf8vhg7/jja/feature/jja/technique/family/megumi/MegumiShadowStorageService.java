package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

final class MegumiShadowStorageService {
    private static final String CONTAINER_TITLE_KEY = "container.jja.megumi_shadow";

    private MegumiShadowStorageService() {
    }

    static ItemStack insert(ServerPlayer owner, ItemStack stack) {
        MegumiShadowStorageData data = MegumiShadowStorageData.get(owner.server);
        return data.insert(owner.getUUID(), stack, resolveCapacity(owner));
    }

    static void open(ServerPlayer owner, BlockPos sourcePos) {
        MegumiShadowStorageData data = MegumiShadowStorageData.get(owner.server);
        int activeSlots = resolveCapacity(owner);
        int rows = MegumiShadowRules.menuRowsFor(activeSlots);
        MenuProvider provider = new SimpleMenuProvider(
            (id, inventory, player) -> new MegumiShadowStorageMenu(
                id,
                inventory,
                new MegumiShadowStorageContainer(data, owner.getUUID(), activeSlots, sourcePos),
                rows,
                activeSlots
            ),
            Component.translatable(CONTAINER_TITLE_KEY)
        );
        NetworkHooks.openScreen(owner, provider, buffer -> {
            buffer.writeVarInt(rows);
            buffer.writeVarInt(activeSlots);
        });
    }

    static int resolveCapacity(ServerPlayer owner) {
        MobEffectInstance strength = owner.getEffect(MobEffects.DAMAGE_BOOST);
        return MegumiShadowRules.storageCapacityFromStrengthAmplifier(strength == null ? null : strength.getAmplifier());
    }
}
