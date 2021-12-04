package me.nikyoff.seasons.mixin.client;

import me.nikyoff.seasons.common.SeasonsHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At(value = "RETURN"), method = "getLeftText")
    private void onGetLeftText(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
        List<String> list = callbackInfoReturnable.getReturnValue();

        list.add("Current season: " + SeasonsHelper.getCurrentSeason(this.client.world).toString());
        list.add("Current temperate season: " + SeasonsHelper.getCurrentTemperateSeason(this.client.world).toString());
        list.add("Current tropical season: " + SeasonsHelper.getCurrentTropicalSeason(this.client.world).toString());
    }
}
