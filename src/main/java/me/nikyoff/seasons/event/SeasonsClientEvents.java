package me.nikyoff.seasons.event;

import me.nikyoff.seasons.common.Season;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class SeasonsClientEvents {
    public static final Event<SeasonChange> SEASON_CHANGE = EventFactory.createArrayBacked(SeasonChange.class, callbacks -> (previousSeason, currentSeason) -> {
        for (SeasonChange callback : callbacks) {
            callback.onChange(previousSeason, currentSeason);
        }
    });

    public static final Event<TemperateSeasonChange> TEMPERATE_SEASON_CHANGE = EventFactory.createArrayBacked(TemperateSeasonChange.class, callbacks -> (previousTemperateSeason, currentTemperateSeason) -> {
        for (TemperateSeasonChange callback : callbacks) {
            callback.onChange(previousTemperateSeason, currentTemperateSeason);
        }
    });

    public static final Event<TropicalSeasonChange> TROPICAL_SEASON_CHANGE = EventFactory.createArrayBacked(TropicalSeasonChange.class, callbacks -> (previousTropicalSeason, currentTropicalSeason) -> {
        for (TropicalSeasonChange callback : callbacks) {
            callback.onChange(previousTropicalSeason, currentTropicalSeason);
        }
    });

    @FunctionalInterface
    public interface SeasonChange {
        void onChange(Season previousSeason, Season currentSeason);
    }

    @FunctionalInterface
    public interface TemperateSeasonChange {
        void onChange(Season.TemperateSeason previousTemperateSeason, Season.TemperateSeason currentTemperateSeason);
    }

    @FunctionalInterface
    public interface TropicalSeasonChange {
        void onChange(Season.TropicalSeason previousTropicalSeason, Season.TropicalSeason currentTropicalSeason);
    }
}
