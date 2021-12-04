package me.nikyoff.seasons.mixin.client;

import me.nikyoff.seasons.common.ColorHelper;
import me.nikyoff.seasons.common.ISeasonColorProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public abstract class MixinBiomeColors {

    @Inject(method = "getFoliageColor", at = @At(value = "RETURN"), cancellable = true)
    private static void getFoliageColorInject(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        Biome biome = clientWorld.getBiome(pos);

        ISeasonColorProvider seasonColorProvider = ColorHelper.getBiomeColorProvider(clientWorld, biome);

        if (seasonColorProvider == null) {
            return;
        }

        callbackInfoReturnable.setReturnValue(ColorHelper.applySeasonalFoliageColouring(seasonColorProvider, ColorHelper.FoliageType.DEFAULT, biome, callbackInfoReturnable.getReturnValue()));
    }

    @Inject(method = "getGrassColor", at = @At(value = "RETURN"), cancellable = true)
    private static void getGrassColorInject(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        Biome biome = clientWorld.getBiome(pos);

        ISeasonColorProvider seasonColorProvider = ColorHelper.getBiomeColorProvider(clientWorld, biome);

        if (seasonColorProvider == null) {
            return;
        }

        callbackInfoReturnable.setReturnValue(ColorHelper.applySeasonalGrassColouring(seasonColorProvider, biome, callbackInfoReturnable.getReturnValue()));
    }
}
