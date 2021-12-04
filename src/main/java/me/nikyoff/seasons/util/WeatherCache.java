package me.nikyoff.seasons.util;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.Map;

public class WeatherCache {
    private static final Map<Identifier, Biome.Weather> cacheMap =  Maps.newHashMap();

    public static boolean hasCache(Identifier identifier) {
        return cacheMap.containsKey(identifier);
    }

    public static Biome.Weather getCache(Identifier identifier) {
        return cacheMap.get(identifier);
    }

    public static void setCache(Identifier identifier, Biome.Weather weather) {
        cacheMap.put(identifier, weather);
    }
}
