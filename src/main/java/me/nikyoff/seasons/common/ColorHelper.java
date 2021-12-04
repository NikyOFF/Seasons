package me.nikyoff.seasons.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ColorHelper {
    @Nullable
    public static ISeasonColorProvider getBiomeColorProvider(ClientWorld clientWorld, Biome biome) {
        ISeasonColorProvider seasonColorProvider;

        if (SeasonsHelper.isTropicalBiome(biome)) {
            seasonColorProvider = SeasonsHelper.getCurrentTropicalSeason(clientWorld);
        } else {
            seasonColorProvider = SeasonsHelper.getCurrentTemperateSeason(clientWorld);
        }

        return seasonColorProvider;
    }

    public static int mixColors(int a, int b, float ratio) {
        if (ratio > 1f) {
            ratio = 1f;
        } else if (ratio < 0f) {
            ratio = 0f;
        }

        float iRatio = 1.0f - ratio;

        int aA = (a >> 24 & 0xff);
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24 & 0xff);
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int A = (int)((aA * iRatio) + (bA * ratio));
        int R = (int)((aR * iRatio) + (bR * ratio));
        int G = (int)((aG * iRatio) + (bG * ratio));
        int B = (int)((aB * iRatio) + (bB * ratio));

        return A << 24 | R << 16 | G << 8 | B;
    }

    public static int multiplyColours(int colour1, int colour2) {
        return (int)((colour1 / 255.0F) * (colour2 / 255.0F) * 255.0F);
    }

    public static int overlayBlendChannel(int underColour, int overColour) {
        int retVal;

        if (underColour < 128) {
            retVal = multiplyColours(2 * underColour, overColour);
        } else {
            retVal = multiplyColours(2 * (255 - underColour), 255 - overColour);
            retVal = 255 - retVal;
        }

        return retVal;
    }

    public static int overlayBlend(int underColour, int overColour) {
        int r = overlayBlendChannel((underColour >> 16) & 255, (overColour >> 16) & 255);
        int g = overlayBlendChannel((underColour >> 8) & 255, (overColour >> 8) & 255);
        int b = overlayBlendChannel(underColour & 255, overColour & 255);

        return (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    public static int saturateColour(int color, float saturationMultiplier) {
        Color newColor = new Color(color);
        float[] hsv = new float[3];

        Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), hsv);
        hsv[1] *= saturationMultiplier;

        return Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
    }

    public static int applySeasonalGrassColouring(ISeasonColorProvider colorProvider, Biome biome, int originalColor) {
        World world = MinecraftClient.getInstance().world;

        if (SeasonsHelper.canApplySeasonalEffects.test(world, biome.toString())) {
            return originalColor;
        }

        return ColorHelper.applySeasonalColouring(colorProvider.getGrassOverlay(), colorProvider.getGrassSaturationMultiplier(), originalColor, biome);
    }

    public static int applySeasonalFoliageColouring(ISeasonColorProvider colorProvider, FoliageType foliageType, Biome biome, int originalColor) {
        World world = MinecraftClient.getInstance().world;

        Identifier biomeId = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);

        if (!SeasonsHelper.canApplySeasonalEffects.test(world, biomeId.toString()) || biome.getCategory() == Biome.Category.UNDERGROUND) {
            return originalColor;
        }

        int color;

        switch (foliageType) {
            case BIRCH -> color = colorProvider.getBirchColor();
            case SPRUCE -> color = colorProvider.getSpruceColor();
            default -> color = colorProvider.getFoliageOverlay();
        }

        return ColorHelper.applySeasonalColouring(color, colorProvider.getFoliageSaturationMultiplier(), originalColor, biome);
    }

    public static int applySeasonalColouring(int overlay, float saturationMultiplier, int originalColor, Biome biome) {
        int color = overlay == 0xFFFFFF ? originalColor : overlayBlend(originalColor, overlay);
        int fixedColor = color;

        if (SeasonsHelper.lessColorChange(biome.toString())) {
            fixedColor = mixColors(color, originalColor, 0.75f);
        }

        return saturationMultiplier != -1 ? saturateColour(fixedColor, saturationMultiplier) : fixedColor;
    }

    public enum FoliageType {
        DEFAULT,
        BIRCH,
        SPRUCE
    }
}
