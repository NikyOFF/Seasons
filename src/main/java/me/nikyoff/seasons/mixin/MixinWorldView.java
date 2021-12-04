package me.nikyoff.seasons.mixin;

import me.nikyoff.seasons.common.Season;
import me.nikyoff.seasons.common.SeasonsHelper;
import me.nikyoff.seasons.util.WeatherCache;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldView.class)
@Debug(export = true)
public interface MixinWorldView {

    @Shadow BiomeAccess getBiomeAccess();

    default void injectBiomeTemperature(World world, Biome biome) {
        String biomeId = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome).toString();

        if (!SeasonsHelper.enablesSeasonalEffects(biomeId)) {
            return;
        }

        Biome.Weather currentWeather = biome.weather;
        Biome.Weather originalWeather;

        Identifier biomeIdentifier = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);

        if (!WeatherCache.hasCache(biomeIdentifier)) {
            originalWeather = new Biome.Weather(currentWeather.precipitation, currentWeather.temperature, currentWeather.temperatureModifier, currentWeather.downfall);
            WeatherCache.setCache(biomeIdentifier, originalWeather);
        } else {
            originalWeather = WeatherCache.getCache(biomeIdentifier);
        }

        if(originalWeather == null) {
            return;
        }

        Season season = SeasonsHelper.getCurrentSeason(world);

        if (season == null) {
            return;
        }

        float temperature = originalWeather.temperature;

        if (SeasonsHelper.isTropicalBiome(biome)) {
            int tropicalSeasonIndex = SeasonsHelper.getCurrentTropicalSeasonIndex((World) this);
            int multiply = tropicalSeasonIndex % Season.TemperateSeason.VALUES.length;

            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature + (Math.max(0.2f, 0.2f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature - (Math.max(0.2f, 0.2f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature);
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
            }

            return;
        }

        int temperateSeasonIndex = SeasonsHelper.getCurrentTemperateSeasonIndex((World) this);
        int multiply = temperateSeasonIndex % Season.TemperateSeason.VALUES.length;

        if (temperature <= 0.1f) {
            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature + (Math.max(0.3f, 0.3f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature - (Math.max(0.3f, 0.3f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                }
                default -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature);
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
            }
        } else if (temperature <= 0.3f) {
            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature + (Math.max(0.2f, 0.2f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                }
                case AUTUMN -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature);
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature - (Math.max(0.2f, 0.2f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                }
                case SPRING -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature);
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.RAIN);
                }
            }
        } else if (temperature <= 0.95f) {
            switch (season) {
                case SUMMER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature + (Math.max(0.7f, 0.7f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
                case AUTUMN -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature - (Math.max(0.1f, 0.1f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
                case WINTER -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature - (Math.max(2f, 2f * multiply)));
                    ((WeatherAccessor) currentWeather).setPrecipitation(Biome.Precipitation.SNOW);
                }
                case SPRING -> {
                    ((WeatherAccessor) currentWeather).setTemperature(temperature);
                    ((WeatherAccessor) currentWeather).setPrecipitation(originalWeather.precipitation);
                }
            }
        }
    }

    /**
     * @author nikyoff
     * @reason wtf i dont need it
     */
    @Overwrite
    default Biome getBiome(BlockPos pos) {
        Biome biome = this.getBiomeAccess().getBiome(pos);

        if (this instanceof World) {
//            System.out.println("inject biome temperature");
            injectBiomeTemperature((World) this, biome);
        }

        return biome;
    }
}
