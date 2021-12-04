package me.nikyoff.seasons.config.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.network.PacketByteBuf;

public class BiomeData {

    @SerializedName("use_tropical_seasons")
    public boolean useTropicalSeasons;

    @SerializedName("enable_seasonal_effects")
    public boolean enableSeasonalEffects;

    @SerializedName("lees_color")
    public boolean lessColor;

    public BiomeData(boolean useTropicalSeasons, boolean enableSeasonalEffects, boolean lessColor) {
        this.useTropicalSeasons = useTropicalSeasons;
        this.enableSeasonalEffects = enableSeasonalEffects;
        this.lessColor = lessColor;
    }

    public static void serialize(PacketByteBuf packetByteBuf, BiomeData biomeData) {
        packetByteBuf.writeBoolean(biomeData.useTropicalSeasons);
        packetByteBuf.writeBoolean(biomeData.enableSeasonalEffects);
        packetByteBuf.writeBoolean(biomeData.lessColor);
    }

    public static BiomeData deserialize(PacketByteBuf packetByteBuf) {
        return new BiomeData( packetByteBuf.readBoolean(),  packetByteBuf.readBoolean(),  packetByteBuf.readBoolean());
    }
}
