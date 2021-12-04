package me.nikyoff.seasons;

import com.mojang.brigadier.CommandDispatcher;
import me.nikyoff.seasons.command.SeasonsCommand;
import me.nikyoff.seasons.common.SeasonsHelper;
import me.nikyoff.seasons.config.BiomeConfig;
import me.nikyoff.seasons.config.SeasonsConfig;
import me.nikyoff.seasons.event.SeasonsServerEvents;
import me.nikyoff.seasons.network.*;
import me.nikyoff.seasons.util.WorldCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SeasonsMod implements ModInitializer {
    public static final String MOD_ID = "seasons";

    public static final Logger LOGGER = LogManager.getLogger(SeasonsMod.MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(SeasonsMod.MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        NetworkHandlerC2S.initialize();
        SeasonsConfig.initialize();
        BiomeConfig.initialize();
        this.registerEvents();
    }

    private void registerEvents() {
        CommandRegistrationCallback.EVENT.register(this::handleCommandRegistration);

        ServerLifecycleEvents.SERVER_STARTED.register(this::handleServerStarted);

        ServerTickEvents.START_SERVER_TICK.register(this::handleServerTick);

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::handleDataPackReload);
    }

    private void handleCommandRegistration(CommandDispatcher<ServerCommandSource> commandDispatcher, boolean dedicated) {
        SeasonsCommand.initialize(commandDispatcher);
    }

    private void handleServerStarted(MinecraftServer minecraftServer) {
        WorldCache.reload(minecraftServer);

        SeasonsServerEvents.SEASON_CHANGE.register((previousSeason, currentSeason) -> {
            SeasonChangeS2CPacket.send(minecraftServer, previousSeason, currentSeason);
        });

        SeasonsServerEvents.TEMPERATE_SEASON_CHANGE.register((previousTemperateSeason, currentTemperateSeason) -> {
            TemperateSeasonChangeS2CPacket.send(minecraftServer, previousTemperateSeason, currentTemperateSeason);
        });

        SeasonsServerEvents.TROPICAL_SEASON_CHANGE.register((previousTropicalSeason, currentTropicalSeason) -> {
            TropicalSeasonChangeS2CPacket.send(minecraftServer, previousTropicalSeason, currentTropicalSeason);
        });
    }

    private void handleServerTick(MinecraftServer minecraftServer) {

        for (World world : WorldCache.getCacheSet()) {
            SeasonsHelper.checkWorld(world);
        }
    }

    private void handleDataPackReload(MinecraftServer minecraftServer, ServerResourceManager serverResourceManager) {
        SeasonsConfig.initialize();
        BiomeConfig.initialize();

        WorldCache.reload(minecraftServer);

        ConfigSyncC2S2CPacket.sendFromServer(minecraftServer);
    }
}
