package me.nikyoff.seasons.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NetworkHandlerS2C {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncC2S2CPacket.IDENTIFIER, ConfigSyncC2S2CPacket::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(SeasonChangeS2CPacket.IDENTIFIER, SeasonChangeS2CPacket::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(TemperateSeasonChangeS2CPacket.IDENTIFIER, TemperateSeasonChangeS2CPacket::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(TropicalSeasonChangeS2CPacket.IDENTIFIER, TropicalSeasonChangeS2CPacket::handleClient);
    }
}
