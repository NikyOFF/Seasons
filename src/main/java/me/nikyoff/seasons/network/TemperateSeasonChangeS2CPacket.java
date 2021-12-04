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

public class TemperateSeasonChangeS2CPacket {
    public static Identifier IDENTIFIER = SeasonsMod.id("temperate_season_change");

    public static void send(MinecraftServer minecraftServer, Season.TemperateSeason previousTemperateSeason, Season.TemperateSeason currentTemperateSeason) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();

        packetByteBuf.writeInt(previousTemperateSeason.ordinal());
        packetByteBuf.writeInt(currentTemperateSeason.ordinal());

        for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(serverPlayerEntity, TemperateSeasonChangeS2CPacket.IDENTIFIER, packetByteBuf);
        }
    }

    public static void handleClient(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int previousTemperateSeasonOrder = packetByteBuf.readInt();
        int currentTemperateSeasonOrder = packetByteBuf.readInt();

        Season.TemperateSeason previousTemperateSeason = Season.TemperateSeason.VALUES[previousTemperateSeasonOrder];
        Season.TemperateSeason currentTemperateSeason = Season.TemperateSeason.VALUES[currentTemperateSeasonOrder];

        minecraftClient.execute(() -> {
            SeasonsClientEvents.TEMPERATE_SEASON_CHANGE.invoker().onChange(previousTemperateSeason, currentTemperateSeason);
        });
    }
}
