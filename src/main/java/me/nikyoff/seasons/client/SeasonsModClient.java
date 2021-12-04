package me.nikyoff.seasons.client;

import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.common.Season;
import me.nikyoff.seasons.common.SeasonsHelper;
import me.nikyoff.seasons.config.BiomeConfig;
import me.nikyoff.seasons.config.SeasonsConfig;
import me.nikyoff.seasons.event.SeasonsClientEvents;
import me.nikyoff.seasons.network.ConfigSyncC2S2CPacket;
import me.nikyoff.seasons.network.NetworkHandlerS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class SeasonsModClient implements ClientModInitializer {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitializeClient() {
        NetworkHandlerS2C.initialize();

        this.registerEvents();
    }

    private void registerEvents() {
        ClientPlayConnectionEvents.JOIN.register(this::handleJoin);

        ClientPlayConnectionEvents.DISCONNECT.register(this::handleDisconnect);

        SeasonsClientEvents.SEASON_CHANGE.register(this::handleSeasonChange);

        SeasonsClientEvents.TEMPERATE_SEASON_CHANGE.register(this::handleTemperateSeasonChange);

        SeasonsClientEvents.TROPICAL_SEASON_CHANGE.register(this::handleTropicalSeasonChange);
    }

    private void handleJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        if (minecraftClient.isIntegratedServerRunning()) {
            return;
        }

        ConfigSyncC2S2CPacket.sendFromClient();
    }

    private void handleDisconnect(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        SeasonsConfig.initialize();
        BiomeConfig.initialize();
    }

    private void handleSeasonChange(Season previousSeason, Season currentSeason) {
        SeasonsMod.LOGGER.info("Season changed");
    }

    private void handleTemperateSeasonChange(Season.TemperateSeason previousTemperateSeason, Season.TemperateSeason currentTemperateSeason) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.player != null && !SeasonsHelper.isTropicalBiome(minecraftClient.player.world.getBiome(minecraftClient.player.getBlockPos()))) {
            this.reloadWorldRenderer();
        }

        SeasonsMod.LOGGER.info("Temperate season changed");
    }

    private void handleTropicalSeasonChange(Season.TropicalSeason previousTropicalSeason, Season.TropicalSeason currentTropicalSeason) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.player != null && SeasonsHelper.isTropicalBiome(minecraftClient.player.world.getBiome(minecraftClient.player.getBlockPos()))) {
            this.reloadWorldRenderer();
        }

        SeasonsMod.LOGGER.info("Tropical season changed");
    }

    public void reloadWorldRenderer() {
        this.executorService.schedule(() -> {
            SeasonsMod.LOGGER.debug("Reload world renderer");

            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            minecraftClient.execute(minecraftClient.worldRenderer::reload);

        }, 1, TimeUnit.SECONDS);
    }
}
