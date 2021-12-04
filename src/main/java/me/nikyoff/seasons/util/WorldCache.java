package me.nikyoff.seasons.util;

import com.google.common.collect.Sets;
import me.nikyoff.seasons.config.SeasonsConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Set;

public class WorldCache {
    private static final Set<World> cacheSet = Sets.newHashSet();

    public static Set<World> getCacheSet() {
        return WorldCache.cacheSet;
    }

    public static boolean hasCache() {
        return !cacheSet.isEmpty();
    }

    public static void reload(MinecraftServer minecraftServer) {
        for (RegistryKey<World> key : minecraftServer.getWorldRegistryKeys()) {
            World world = minecraftServer.getWorld(key);

            if (world == null) {
                continue;
            }

            if (!SeasonsConfig.isWhitelistedDimension(world)) {
                continue;
            }

            WorldCache.cacheSet.add(world);
        }
    }
}
