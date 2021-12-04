package me.nikyoff.seasons.network;

import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.config.BiomeConfig;
import me.nikyoff.seasons.config.SeasonsConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConfigSyncC2S2CPacket {
    public static Identifier IDENTIFIER = SeasonsMod.id("config_sync");

    public static PacketByteBuf write() {
        PacketByteBuf packetByteBufToClient = PacketByteBufs.create();

        packetByteBufToClient.writeString(SeasonsConfig.toJSON(SeasonsConfig.INSTANCE));
        packetByteBufToClient.writeString(BiomeConfig.toJSON(BiomeConfig.INSTANCE));

        return packetByteBufToClient;
    }

    public static void sendFromClient() {
        ClientPlayNetworking.send(ConfigSyncC2S2CPacket.IDENTIFIER, PacketByteBufs.empty());
    }

    public static void sendFromServer(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(serverPlayerEntity ->
                ServerPlayNetworking.send(serverPlayerEntity, ConfigSyncC2S2CPacket.IDENTIFIER, ConfigSyncC2S2CPacket.write())
        );
    }

    public static void handleServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        minecraftServer.execute(() -> packetSender.sendPacket(ConfigSyncC2S2CPacket.IDENTIFIER, ConfigSyncC2S2CPacket.write()));
    }

    public static void handleClient(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String seasonsConfigJson = packetByteBuf.readString();
        String biomeConfigJson = packetByteBuf.readString();

        minecraftClient.execute(() -> {
            SeasonsConfig.INSTANCE = SeasonsConfig.fromJSON(seasonsConfigJson);
            BiomeConfig.INSTANCE = BiomeConfig.fromJSON(biomeConfigJson);
        });
    }
}
