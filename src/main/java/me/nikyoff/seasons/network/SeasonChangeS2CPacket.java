package me.nikyoff.seasons.network;

import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.common.Season;
import me.nikyoff.seasons.event.SeasonsClientEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SeasonChangeS2CPacket {
    public static Identifier IDENTIFIER = SeasonsMod.id("season_change");

    public static void send(MinecraftServer minecraftServer, Season previousSeason, Season currentSeason) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();

        packetByteBuf.writeInt(previousSeason.ordinal());
        packetByteBuf.writeInt(currentSeason.ordinal());

        for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(serverPlayerEntity, SeasonChangeS2CPacket.IDENTIFIER, packetByteBuf);
        }
    }

    public static void handleClient(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        Season previousSeason = Season.VALUES[packetByteBuf.readInt()];
        Season currentSeason = Season.VALUES[packetByteBuf.readInt()];

        minecraftClient.execute(() -> {
            SeasonsClientEvents.SEASON_CHANGE.invoker().onChange(previousSeason, currentSeason);
        });
    }
}
