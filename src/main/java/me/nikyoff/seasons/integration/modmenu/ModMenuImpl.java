package me.nikyoff.seasons.integration.modmenu;

import com.google.common.collect.Maps;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.nikyoff.seasons.SeasonsMod;
import me.nikyoff.seasons.common.Season;
import me.nikyoff.seasons.config.BiomeConfig;
import me.nikyoff.seasons.config.SeasonsConfig;
import me.nikyoff.seasons.config.data.BiomeData;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.commons.compress.utils.Lists;

import java.util.Map;
import java.util.function.Function;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder configBuilder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText(String.format("cloth.%s.title", SeasonsMod.MOD_ID)));

            this.getGeneralConfigCategory(configBuilder);

            this.getBiomesConfigCategory(configBuilder);

            configBuilder.setSavingRunnable(() -> {
                SeasonsConfig.writeConfig(SeasonsConfig.INSTANCE);
                BiomeConfig.writeConfig(BiomeConfig.INSTANCE);
            });

            return configBuilder.build();
        };
    }

    public ConfigCategory getGeneralConfigCategory(ConfigBuilder configBuilder) {
        ConfigCategory generalConfigCategory = configBuilder.getOrCreateCategory(new TranslatableText(String.format("cloth.%s.category.general", SeasonsMod.MOD_ID)));
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        Function<String, TranslatableText> translatableText = path -> new TranslatableText(String.format("cloth.%s.category.general.%s", SeasonsMod.MOD_ID, path));

        generalConfigCategory.addEntry(
                entryBuilder.startBooleanToggle(translatableText.apply("progressSeasonWhileOffline.name"), SeasonsConfig.INSTANCE.progressSeasonWhileOffline)
                        .setDefaultValue(true)
                        .setTooltip(translatableText.apply("progressSeasonWhileOffline.tooltip"))
                        .setSaveConsumer(progressSeasonWhileOffline -> SeasonsConfig.INSTANCE.progressSeasonWhileOffline = progressSeasonWhileOffline)
                        .build()
        );

        generalConfigCategory.addEntry(
                entryBuilder.startIntField(translatableText.apply("temperateSeasonDuration.name"), SeasonsConfig.INSTANCE.temperateSeasonDuration)
                        .setDefaultValue(8)
                        .setTooltip(translatableText.apply("subSeasonDuration.tooltip"))
                        .setSaveConsumer(temperateSeasonDuration -> SeasonsConfig.INSTANCE.temperateSeasonDuration = temperateSeasonDuration)
                        .build()
        );

        generalConfigCategory.addEntry(
                entryBuilder.startIntField(translatableText.apply("tropicalSeasonDuration.name"), SeasonsConfig.INSTANCE.tropicalSeasonDuration)
                        .setDefaultValue(16)
                        .setTooltip(translatableText.apply("tropicalSeasonDuration.tooltip"))
                        .setSaveConsumer(tropicalSeasonDuration -> SeasonsConfig.INSTANCE.tropicalSeasonDuration = tropicalSeasonDuration)
                        .build()
        );

        generalConfigCategory.addEntry(
                entryBuilder.startEnumSelector(translatableText.apply("startingSubSeason.name"), Season.TemperateSeason.class, SeasonsConfig.INSTANCE.startingSubSeason)
                        .setDefaultValue(Season.TemperateSeason.EARLY_SPRING)
                        .setTooltip(translatableText.apply("startingSubSeason.tooltip"))
                        .setSaveConsumer(subSeasons -> SeasonsConfig.INSTANCE.startingSubSeason = subSeasons)
                        .build()
        );

        generalConfigCategory.addEntry(
                entryBuilder.startStrList(translatableText.apply("whitelistedDimensions.name"), SeasonsConfig.INSTANCE.whitelistedDimensions.stream().toList())
                        .setDefaultValue(Lists.newArrayList())
                        .setTooltip(translatableText.apply("whitelistedDimensions.tooltip"))
                        .setSaveConsumer(whitelistedDimensions -> {
                            SeasonsConfig.INSTANCE.whitelistedDimensions.clear();
                            SeasonsConfig.INSTANCE.whitelistedDimensions.addAll(whitelistedDimensions);
                        })
                        .build()
        );

        return generalConfigCategory;
    }

    public ConfigCategory getBiomesConfigCategory(ConfigBuilder configBuilder) {
        ConfigCategory biomesConfigCategory = configBuilder.getOrCreateCategory(new TranslatableText(String.format("cloth.%s.category.biomes", SeasonsMod.MOD_ID)));
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        Function<String, TranslatableText> translatableText = path -> new TranslatableText(String.format("cloth.%s.category.biomes.%s", SeasonsMod.MOD_ID, path));

        biomesConfigCategory.addEntry(
                entryBuilder.startStrList(translatableText.apply("biomeDataKeys.name"), BiomeConfig.INSTANCE.biomeDataMap.keySet().stream().toList())
                        .setDefaultValue(Lists.newArrayList())
                        .setTooltip(translatableText.apply("biomeDataKeys.tooltip"))
                        .setSaveConsumer(keys -> {
                            Map<String, BiomeData> biomeDataMap = Maps.newHashMap();

                            for (String key : keys) {
                                if (key.length() <= 0) {
                                    continue;
                                }

                                biomeDataMap.put(key, BiomeConfig.INSTANCE.biomeDataMap.getOrDefault(key, new BiomeData(false, true, false)));
                            }

                            BiomeConfig.INSTANCE.biomeDataMap.clear();
                            BiomeConfig.INSTANCE.biomeDataMap.putAll(biomeDataMap);
                        })
                        .build()
        );

        for (Map.Entry<String, BiomeData> entry : BiomeConfig.INSTANCE.biomeDataMap.entrySet()) {
            BiomeData biomeData = entry.getValue();
            SubCategoryBuilder subCategoryBuilder = entryBuilder.startSubCategory(new LiteralText(entry.getKey()));

            subCategoryBuilder.add(
                    entryBuilder.startBooleanToggle(translatableText.apply("biomeDataKeys.settings.useTropicalSeasons.name"), biomeData.useTropicalSeasons)
                            .setDefaultValue(false)
                            .setTooltip(translatableText.apply("biomeDataKeys.settings.useTropicalSeasons.tooltip"))
                            .setSaveConsumer(useTropicalSeasons -> biomeData.useTropicalSeasons = useTropicalSeasons)
                            .build()
            );

            subCategoryBuilder.add(
                    entryBuilder.startBooleanToggle(translatableText.apply("biomeDataKeys.settings.enableSeasonalEffects.name"), biomeData.enableSeasonalEffects)
                            .setDefaultValue(false)
                            .setTooltip(translatableText.apply("biomeDataKeys.settings.enableSeasonalEffects.tooltip"))
                            .setSaveConsumer(enableSeasonalEffects -> biomeData.enableSeasonalEffects = enableSeasonalEffects)
                            .build()
            );

            subCategoryBuilder.add(
                    entryBuilder.startBooleanToggle(translatableText.apply("biomeDataKeys.settings.lessColor.name"), biomeData.lessColor)
                            .setDefaultValue(false)
                            .setTooltip(translatableText.apply("biomeDataKeys.settings.lessColor.tooltip"))
                            .setSaveConsumer(lessColor -> biomeData.lessColor = lessColor)
                            .build()
            );

            biomesConfigCategory.addEntry(subCategoryBuilder.build());
        }

        return biomesConfigCategory;
    }
}
