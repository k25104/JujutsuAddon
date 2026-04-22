package com.arf8vhg7.jja.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

public final class JjaOptionalModHelper {
    private JjaOptionalModHelper() {
    }

    public static boolean isLoaded(String modId) {
        LoadingModList loadingModList = LoadingModList.get();
        if (loadingModList != null) {
            return loadingModList.getModFileById(modId) != null;
        }

        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(modId);
    }
}
