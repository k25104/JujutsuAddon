package com.arf8vhg7.jja.mixin.curios.client;

import com.arf8vhg7.jja.hook.curios.client.CuriosTooltipHook;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "top.theillusivec4.curios.api.CuriosTooltip", remap = false)
public abstract class CuriosTooltipMixin {
    @ModifyArg(
        method = "appendSlotHeader",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        ),
        index = 0,
        remap = false,
        require = 1
    )
    private String jja$resolveLegsHeaderKey(String translationKey) {
        return CuriosTooltipHook.resolveModifierHeaderKey(translationKey);
    }

    @Inject(method = "build", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private void jja$normalizeBuiltTooltip(CallbackInfoReturnable<List<Component>> cir) {
        cir.setReturnValue(CuriosTooltipHook.normalizeTooltip(cir.getReturnValue()));
    }
}