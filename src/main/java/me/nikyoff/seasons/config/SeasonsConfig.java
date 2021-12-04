package me.nikyoff.seasons.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.common.Season;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

public class SeasonsConfig {
    public static SeasonsConfig INSTANCE;
    private static final File FILE = new File("config/seasons/seasons.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Integer temperateSeasonDuration;
    public Integer tropicalSeasonDuration;
    public Season.TemperateSeason startingSubSeason;
    public HashSet<String> whitelistedDimensions;

    public boolean progressSeasonWhileOffline;

    public SeasonsConfig() {
        this.temperateSeasonDuration = 8;
        this.tropicalSeasonDuration = 16;
        this.startingSubSeason = Season.TemperateSeason.EARLY_SPRING;
        this.whitelistedDimensions = new HashSet<>(Arrays.asList("minecraft:overworld"));
        this.progressSeasonWhileOffline = false;
    }

    public SeasonsConfig(int temperateSeasonDuration, int tropicalSeasonDuration, @NotNull Season.TemperateSeason startingSubSeason, @NotNull HashSet<String> whitelistedDimensions, boolean progressSeasonWhileOffline) {
        this.temperateSeasonDuration = temperateSeasonDuration;
        this.tropicalSeasonDuration = tropicalSeasonDuration;
        this.startingSubSeason = startingSubSeason;
        this.whitelistedDimensions = whitelistedDimensions;
        this.progressSeasonWhileOffline = progressSeasonWhileOffline;
    }

    public static String toJSON(SeasonsConfig seasonsConfig) {
        return SeasonsConfig.GSON.toJson(seasonsConfig);
    }

    public static SeasonsConfig fromJSON(String json) {
        return SeasonsConfig.GSON.fromJson(json, SeasonsConfig.class);
    }

    public static void initialize() {
        SeasonsMod.LOGGER.info("Initialize Seasons config");

        try {
            if (!SeasonsConfig.FILE.getParentFile().exists()) {
                SeasonsConfig.FILE.getParentFile().mkdirs();
            }

            if (!SeasonsConfig.FILE.exists()) {
                SeasonsConfig.initializeDefault();
            }

            SeasonsConfig.readConfig();

            SeasonsMod.LOGGER.info("Seasons config initialized!");
        } catch (Exception exception) {
            SeasonsMod.LOGGER.error("Seasons config initialized error", exception);
        }
    }

    public static void initializeDefault() {
        SeasonsMod.LOGGER.info("Initialize default config");

        if (!SeasonsConfig.FILE.exists()) {
            try {
                SeasonsConfig.FILE.createNewFile();
            } catch (Exception exception) {
                SeasonsMod.LOGGER.error(exception.getMessage());
                return;
            }
        }

        SeasonsConfig.writeConfig(new SeasonsConfig());

        SeasonsMod.LOGGER.info("Default config initialized");
    }

    public static void writeConfig(SeasonsConfig seasonsConfig) {
        SeasonsMod.LOGGER.info("Writing Seasons config");

        try (Writer writer = Files.newWriter(SeasonsConfig.FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(seasonsConfig, writer);
            SeasonsMod.LOGGER.info("Seasons config recorded");
        } catch (Exception exception) {
            SeasonsMod.LOGGER.error(exception.getMessage());
        }
    }

    public static void readConfig() {
        SeasonsMod.LOGGER.info("Reading Seasons Config");

        try(BufferedReader reader = Files.newReader(SeasonsConfig.FILE, StandardCharsets.UTF_8)) {
            SeasonsConfig.INSTANCE = SeasonsConfig.GSON.fromJson(reader, SeasonsConfig.class);
            SeasonsMod.LOGGER.info("Seasons config read");
        } catch (Exception exception) {
            SeasonsMod.LOGGER.error(exception.getMessage());
        }
    }

    public static boolean isWhitelistedDimension(World world) {
        return SeasonsConfig.INSTANCE.whitelistedDimensions.contains(world.getRegistryKey().getValue().toString());
    }
}
