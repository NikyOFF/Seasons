package me.nikyoff.seasons.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NetworkHandlerC2S {
    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncC2S2CPacket.IDENTIFIER, ConfigSyncC2S2CPacket::handleServer);
    }
}
