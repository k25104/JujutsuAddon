package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.KeyUseMainSkillMessageAccess;
import com.arf8vhg7.jja.hook.jujutsucraft.KeyUseMainSkillMessageHook;
import java.util.function.Supplier;
import net.mcreator.jujutsucraft.network.KeyUseMainSkillMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyUseMainSkillMessage.class, remap = false)
public abstract class KeyUseMainSkillMessageMixin implements KeyUseMainSkillMessageAccess {
    @Shadow
    int type;

    @Shadow
    int pressedms;

    @Unique
    private int pressedSlot = 0;

    @Override
    public int jja$getPressedSlot() {
        return this.pressedSlot;
    }

    @Override
    public void jja$setPressedSlot(int pressedSlot) {
        this.pressedSlot = pressedSlot;
    }

    @Inject(method = "<init>(II)V", at = @At("RETURN"), remap = false, require = 1)
    private void jja$initPressedSlot(int type, int pressedms, CallbackInfo ci) {
        this.pressedSlot = 0;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = false, require = 1)
    private void jja$readPressedSlot(FriendlyByteBuf buffer, CallbackInfo ci) {
        if (this.type == 0 && buffer.readableBytes() >= Integer.BYTES) {
            this.pressedSlot = buffer.readInt();
        } else {
            this.pressedSlot = 0;
        }
    }

    @Inject(method = "buffer", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$writePressedSlot(KeyUseMainSkillMessage message, FriendlyByteBuf buffer, CallbackInfo ci) {
        int type = ((KeyUseMainSkillMessageMixin) (Object) message).type;
        if (type == 0 && message instanceof KeyUseMainSkillMessageAccess access) {
            buffer.writeInt(access.jja$getPressedSlot());
        }
    }

    @Inject(method = "handler", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$handle(
        KeyUseMainSkillMessage message,
        Supplier<Context> contextSupplier,
        CallbackInfo ci
    ) {
        Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (player == null) {
                return;
            }
            if (message instanceof KeyUseMainSkillMessageAccess access && ((KeyUseMainSkillMessageMixin) (Object) message).type == 0) {
                if (!KeyUseMainSkillMessageHook.handlePress(player, access.jja$getPressedSlot())) {
                    return;
                }
            }
            KeyUseMainSkillMessage.pressAction(player, ((KeyUseMainSkillMessageMixin) (Object) message).type, ((KeyUseMainSkillMessageMixin) (Object) message).pressedms);
        });
        context.setPacketHandled(true);
        ci.cancel();
    }
}
