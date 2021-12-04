package me.nikyoff.seasons.common;

import com.google.common.collect.Maps;
import me.nikyoff.seasons.config.BiomeConfig;
import me.nikyoff.seasons.config.SeasonsConfig;
import me.nikyoff.seasons.config.data.BiomeData;
import me.nikyoff.seasons.event.SeasonsServerEvents;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.util.Map;
import java.util.function.BiPredicate;

public class SeasonsHelper {
    public static final BiPredicate<World, String> canApplySeasonalEffects = (world, biomeId) -> world != null && SeasonsConfig.isWhitelistedDimension(world) && SeasonsHelper.enablesSeasonalEffects(biomeId);

    private static Map<World, Triplet<Season, Season.TemperateSeason, Season.TropicalSeason>> seasonCache = Maps.newHashMap();

    public static Map<World, Triplet<Season, Season.TemperateSeason, Season.TropicalSeason>> getSeasonCache() {
        return SeasonsHelper.seasonCache;
    }

    public static int getTemperateSeasonLength() {
        return SeasonsConfig.INSTANCE.temperateSeasonDuration;
    }

    public static int getSeasonsLength() {
        return SeasonsHelper.getTemperateSeasonLength() * 3;
    }

    public static boolean isTropicalBiome(Biome biome) {
        String biomeId = biome.toString();

        if (BiomeConfig.INSTANCE.biomeDataMap.containsKey(biomeId)) {
            return BiomeConfig.INSTANCE.biomeDataMap.get(biomeId).useTropicalSeasons;
        }

        return biome.getTemperature() > 0.8F;
    }

    public static boolean enablesSeasonalEffects(String biomeId) {

        if (BiomeConfig.INSTANCE.biomeDataMap.containsKey(biomeId)) {
            BiomeData biomeData = BiomeConfig.INSTANCE.biomeDataMap.get(biomeId);
            return biomeData.enableSeasonalEffects;
        }

        return true;
    }

    public static boolean lessColorChange(String biomeId) {

        if (BiomeConfig.INSTANCE.biomeDataMap.containsKey(biomeId)) {
            BiomeData biomeData = BiomeConfig.INSTANCE.biomeDataMap.get(biomeId);
            return biomeData.lessColor;
        }

        return false;
    }

    public static void checkWorld(World world) {
        Season currentSeason = SeasonsHelper.getCurrentSeason(world);
        Season.TemperateSeason currentTemperateSeason = SeasonsHelper.getCurrentTemperateSeason(world);
        Season.TropicalSeason currentTropicalSeason = SeasonsHelper.getCurrentTropicalSeason(world);

        if (!SeasonsHelper.seasonCache.containsKey(world)) {
            SeasonsHelper.seasonCache.put(world, new Triplet<>(currentSeason, currentTemperateSeason, currentTropicalSeason));
            return;
        }

        Triplet<Season, Season.TemperateSeason, Season.TropicalSeason> triplet =  SeasonsHelper.seasonCache.get(world);

        boolean changed = false;

        if (triplet.getA() != currentSeason) {
            SeasonsServerEvents.SEASON_CHANGE.invoker().onChange(triplet.getA(), currentSeason);
            changed = true;
        }

        if (triplet.getB() != currentTemperateSeason) {
            SeasonsServerEvents.TEMPERATE_SEASON_CHANGE.invoker().onChange(triplet.getB(), currentTemperateSeason);
            changed = true;
        }

        if (triplet.getC() != currentTropicalSeason) {
            SeasonsServerEvents.TROPICAL_SEASON_CHANGE.invoker().onChange(triplet.getC(), currentTropicalSeason);
            changed = true;
        }

        if (changed) {
            SeasonsHelper.seasonCache.put(world, new Triplet<>(currentSeason, currentTemperateSeason, currentTropicalSeason));
        }
    }

    @Nullable
    public static Season getCurrentSeason(World world) {
        if (SeasonsConfig.isWhitelistedDimension(world)) {
            int days = Math.toIntExact(world.getTimeOfDay() / 24000);
            int seasonTime = days / SeasonsHelper.getSeasonsLength();
            return Season.VALUES[seasonTime % Season.VALUES.length];
        }

        return null;
    }

    public static int getCurrentTemperateSeasonIndex(World world) {
        if (SeasonsConfig.isWhitelistedDimension(world)) {
            int days = Math.toIntExact(world.getTimeOfDay() / 24000);
            int seasonTime = days / SeasonsHelper.getTemperateSeasonLength();
            return seasonTime % Season.TemperateSeason.VALUES.length;
        }

        return -1;
    }

    public static int getCurrentTropicalSeasonIndex(World world) {
        if (SeasonsConfig.isWhitelistedDimension(world)) {
            int days = Math.toIntExact(world.getTimeOfDay() / 24000);
            int seasonTime = days / SeasonsHelper.getTemperateSeasonLength();
            return seasonTime % Season.TropicalSeason.VALUES.length;
        }

        return -1;
    }

    @Nullable
    public static Season.TemperateSeason getCurrentTemperateSeason(World world) {
        int index = SeasonsHelper.getCurrentTemperateSeasonIndex(world);

        if (index != -1) {
            return Season.TemperateSeason.VALUES[index];
        }

        return null;
    }

    @Nullable
    public static Season.TropicalSeason getCurrentTropicalSeason(World world) {
        int index = SeasonsHelper.getCurrentTropicalSeasonIndex(world);

        if (index != -1) {
            return Season.TropicalSeason.VALUES[index];
        }

        return null;
    }
}
