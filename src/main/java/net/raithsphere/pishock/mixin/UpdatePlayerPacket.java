package net.raithsphere.pishock.mixin;

import net.raithsphere.pishock.message.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSetHealthPacket.class)
public abstract class UpdatePlayerPacket {

    @Shadow
    public abstract float getHealth();

    @Inject(at = @At(value = "HEAD"), method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", cancellable = true)
    public void handle(ClientGamePacketListener p_132101_, CallbackInfo ci) {

        try {
            if (NetworkHandler.last == 0) {
                NetworkHandler.last = this.getHealth();
                return;
            }

            if(NetworkHandler.last == this.getHealth())
                return;

            float newHealth = this.getHealth();
            float diff = NetworkHandler.last - newHealth;

            if (diff > 0) {
                NetworkHandler.post(diff, newHealth, Minecraft.getInstance().player.getMaxHealth(),0);
            }

            NetworkHandler.last = this.getHealth();
        } catch (Exception e) {
        }
    }

}
