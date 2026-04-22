package com.arf8vhg7.jja.mixin.curios.client;

import com.arf8vhg7.jja.hook.curios.client.ClientEventHandlerHook;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "top.theillusivec4.curios.client.ClientEventHandler", remap = false)
public abstract class ClientEventHandlerMixin {
    @Inject(method = "onTooltip", at = @At("TAIL"), remap = false, require = 1)
    private void jja$normalizeCuriosTooltip(ItemTooltipEvent event, CallbackInfo ci) {
        ClientEventHandlerHook.normalizeTooltip(event.getToolTip());
    }
}