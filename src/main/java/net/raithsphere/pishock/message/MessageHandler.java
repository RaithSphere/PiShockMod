package net.raithsphere.pishock.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.raithsphere.pishock.pishock;

import java.util.Optional;
import java.util.function.Supplier;

public class MessageHandler {
    private static String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(pishock.MOD_ID, "packets"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        NETWORK.registerMessage(0, MessageDamageToClient.class, MessageDamageToClient::encode, MessageDamageToClient::decode, MessageDamageToClient::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static class MessageDamageToClient {
        public final float damage, now, max;
        public final int isAlive;

        public MessageDamageToClient(float damage, float now, float max, int isAlive) {
            this.damage = damage;
            this.now = now;
            this.max = max;
            this.isAlive = isAlive;
        }

        public static void encode(MessageDamageToClient packet, FriendlyByteBuf byteBuf) {
            byteBuf.writeFloat(packet.damage);
            byteBuf.writeFloat(packet.now);
            byteBuf.writeFloat(packet.max);
            byteBuf.writeInt(packet.isAlive);
        }

        public static MessageDamageToClient decode(FriendlyByteBuf byteBuf) {
            return new MessageDamageToClient(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readInt());
        }

        public static void handle(MessageDamageToClient packet, Supplier<NetworkEvent.Context> ctx) {
            NetworkHandler.post(packet.damage, packet.now, packet.max, packet.isAlive);
        }
    }
}
