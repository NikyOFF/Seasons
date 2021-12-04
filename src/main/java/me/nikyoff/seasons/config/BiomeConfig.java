package me.nikyoff.seasons.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.config.data.BiomeData;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BiomeConfig {
    public static BiomeConfig INSTANCE;
    private static final File FILE = new File("config/seasons/biome.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @SerializedName("biomes_data")
    public Map<String, BiomeData> biomeDataMap = Maps.newHashMap();

    public BiomeConfig() {
        BiomeConfig.addBlacklistedBiomes(this.biomeDataMap);
        BiomeConfig.addTropicalBiomes(this.biomeDataMap);
    }

    public static String toJSON(BiomeConfig biomeConfig) {
        return BiomeConfig.GSON.toJson(biomeConfig);
    }

    public static BiomeConfig fromJSON(String json) {
        return BiomeConfig.GSON.fromJson(json, BiomeConfig.class);
    }

    public static void initialize() {
        SeasonsMod.LOGGER.info("Initialize biome config");

        try {
            if (!BiomeConfig.FILE.getParentFile().exists()) {
                BiomeConfig.FILE.getParentFile().mkdirs();
            }

            if (!BiomeConfig.FILE.exists()) {
                BiomeConfig.initializeDefault();
            }

            BiomeConfig.readConfig();

            SeasonsMod.LOGGER.info("Biome config initialized!");
        } catch (Exception exception) {
            SeasonsMod.LOGGER.error("Biome config initialized error", exception);
        }
    }

    public static void initializeDefault() {
        SeasonsMod.LOGGER.info("Initialize default biome config");

        if (!BiomeConfig.FILE.exists()) {
            try {
                BiomeConfig.FILE.createNewFile();
            } catch (Exception exception) {
                SeasonsMod.LOGGER.error(exception.getMessage());
                return;
            }
        }

        BiomeConfig.writeConfig(new BiomeConfig());

        SeasonsMod.LOGGER.info("Default biome config initialized");
    }

    public static void writeConfig(BiomeConfig seasonsConfig) {
        SeasonsMod.LOGGER.info("Writing biomes config");

        try (Writer writer = Files.newWriter(BiomeConfig.FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(seasonsConfig, writer);
            SeasonsMod.LOGGER.info("Biomes config recorded");
        } catch (Exception exception) {
            SeasonsMod.LOGGER.error(exception.getMessage());
        }
    }

    public static void readConfig() {
        SeasonsMod.LOGGER.info("Reading biome config");

        try(BufferedReader reader = Files.newReader(BiomeConfig.FILE, StandardCharsets.UTF_8)) {
            BiomeConfig.INSTANCE = BiomeConfig.GSON.fromJson(reader, BiomeConfig.class);
            SeasonsMod.LOGGER.info("Biome config read");
        } catch (Exception exception) {
            SeasonsMod.LOGGER.error(exception.getMessage());
        }
    }

    private static void addBlacklistedBiomes(Map<String, BiomeData> map) {
        List<String> blacklistedBiomes = Lists.newArrayList(
                "minecraft:mushroom_fields",
                "minecraft:mushroom_fields_shore",
                "minecraft:ocean",
                "minecraft:deep_ocean",
                "minecraft:frozen_ocean",
                "minecraft:deep_frozen_ocean",
                "minecraft:cold_ocean",
                "minecraft:deep_cold_ocean",
                "minecraft:lukewarm_ocean",
                "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean",
                "minecraft:deep_warm_ocean",
                "minecraft:river",
                "minecraft:the_void"
        );

        for (String biomeName : blacklistedBiomes) {
            if (!map.containsKey(biomeName)) {
                map.put(biomeName, new BiomeData(false, false, true));
            } else {
                map.get(biomeName).enableSeasonalEffects = false;
            }
        }
    }

    private static void addTropicalBiomes(Map<String, BiomeData> map) {
        List<String> tropicalBiomes = Lists.newArrayList(
                "minecraft:swamp",
                "minecraft:swamp_hills",
                "minecraft:warm_ocean",
                "minecraft:deep_warm_ocean"
        );

        for (String biomeName : tropicalBiomes) {
            if (!map.containsKey(biomeName)) {
                map.put(biomeName, new BiomeData(true, true, false));
            } else {
                map.get(biomeName).useTropicalSeasons = true;
            }
        }
    }
}
