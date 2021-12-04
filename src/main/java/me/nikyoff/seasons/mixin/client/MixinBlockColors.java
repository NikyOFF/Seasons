package me.nikyoff.seasons.mixin.client;

import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.common.ColorHelper;
import me.nikyoff.seasons.common.ISeasonColorProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
@Debug(export = true)
public class MixinBlockColors {

    @Inject(method = "method_1687", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/FoliageColors;getBirchColor()I"), cancellable = true)
    private static void injectBirchColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> info) {
        int birchColor = FoliageColors.getBirchColor();

        try {
            ClientWorld clientWorld = MinecraftClient.getInstance().world;
            Biome biome = clientWorld.getBiome(pos);
            ISeasonColorProvider seasonColorProvider = ColorHelper.getBiomeColorProvider(clientWorld, biome);

            if (seasonColorProvider != null) {
                SeasonsMod.LOGGER.info("apply color");

                info.setReturnValue(ColorHelper.applySeasonalFoliageColouring(seasonColorProvider, ColorHelper.FoliageType.DEFAULT, biome, birchColor));
                return;
            }

            info.setReturnValue(birchColor);
        } catch (Exception exception) {
            info.setReturnValue(birchColor);
        }


    }
}
