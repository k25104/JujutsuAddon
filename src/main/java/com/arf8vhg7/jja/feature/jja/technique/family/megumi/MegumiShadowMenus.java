package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MegumiShadowMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JujutsuAddon.MODID);

    public static final RegistryObject<MenuType<MegumiShadowStorageMenu>> SHADOW_STORAGE = MENU_TYPES.register(
        "megumi_shadow_storage",
        () -> IForgeMenuType.create(MegumiShadowStorageMenu::new)
    );

    private MegumiShadowMenus() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
