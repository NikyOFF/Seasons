package me.nikyoff.seasons.common;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    public static final Season[] VALUES = Season.values();

    public enum TemperateSeason implements ISeasonColorProvider {
        EARLY_SPRING(SPRING, 0x778087, 0.85F, 0x6F818F, 0.85F, 0x869A68, 0x6F818F),
        MID_SPRING(SPRING, 0x678297, 0x4F86AF, 0x6EB283, 0x4F86AF),
        LATE_SPRING(SPRING, 0x6F818F, 0x5F849F, 0x74AE73, 0x5F849F),
        EARLY_SUMMER(SUMMER, 0x778087, 0x6F818F, 0x7AAA64, 0x6F818F),
        MID_SUMMER(SUMMER, 0xFFFFFF, 0xFFFFFF, 0x80A755, 0xFFFFFF),
        LATE_SUMMER(SUMMER, 0x877777, 0x9F5F5F, 0x98A54B, 0x9F5F5F),
        EARLY_AUTUMN(AUTUMN, 0x8F6F6F, 0xC44040, 0xB1A442, 0xC44040),
        MID_AUTUMN(AUTUMN, 0x9F5F5F, 0xEF2121, 0xE2A231, 0xEF2121),
        LATE_AUTUMN(AUTUMN, 0xAF4F4F, 0.85F, 0xDB3030, 0.85F, 0xC98A35, 0xDB3030),
        EARLY_WINTER(WINTER, 0xAF4F4F, 0.60F, 0xDB3030, 0.60F, 0xB1723B, 0xDB3030),
        MID_WINTER(WINTER, 0xAF4F4F, 0.45F, 0xDB3030, 0.45F, 0xA0824D, 0xDB3030),
        LATE_WINTER(WINTER, 0x8E8181, 0.60F, 0xA57070, 0.60F, 0x8F925F, 0xA57070);

        public static final TemperateSeason[] VALUES = TemperateSeason.values();

        private Season season;
        private int grassOverlay;
        private float grassSaturationMultiplier;
        private int foliageOverlay;
        private float foliageSaturationMultiplier;
        private int birchColor;
        private int spruceColor;

        TemperateSeason(Season season, int grassColor, float grassSaturation, int foliageColour, float foliageSaturation, int birchColor, int spruceColor) {
            this.season = season;
            this.grassOverlay = grassColor;
            this.grassSaturationMultiplier = grassSaturation;
            this.foliageOverlay = foliageColour;
            this.foliageSaturationMultiplier = foliageSaturation;
            this.birchColor = birchColor;
            this.spruceColor = spruceColor;
        }

        TemperateSeason(Season season, int grassColor, int foliageColour, int birchColor, int spruceColor) {
            this(season, grassColor, -1, foliageColour, -1, birchColor, spruceColor);
        }

        public Season getSeason() {
            return this.season;
        }

        public int getGrassOverlay() {
            return this.grassOverlay;
        }

        public float getGrassSaturationMultiplier() {
            return this.grassSaturationMultiplier;
        }

        public int getFoliageOverlay() {
            return this.foliageOverlay;
        }

        public float getFoliageSaturationMultiplier() {
            return this.foliageSaturationMultiplier;
        }

        public int getBirchColor() {
            return this.birchColor;
        }

        public int getSpruceColor() {
            return this.spruceColor;
        }
    }

    public enum TropicalSeason implements ISeasonColorProvider {
        EARLY_DRY(0xFFFFFF, 0xFFFFFF, 0x80A755, 0xFFFFFF),
        MID_DRY(0xA58668, 0.8F, 0xB7867C, 0.95F, 0x98A54B, 0xB7867C),
        LATE_DRY(0x8E7B6D, 0.9F, 0xA08B86, 0.975F, 0x80A755, 0xA08B86),
        EARLY_WET(0x758C8A, 0x728C91, 0x80A755, 0x728C91),
        MID_WET(0x548384, 0x2498AE, 0x76AC6C, 0x2498AE),
        LATE_WET(0x658989, 0x4E8893, 0x80A755, 0x4E8893);

        public static final TropicalSeason[] VALUES = TropicalSeason.values();

        private int grassOverlay;
        private float grassSaturationMultiplier;
        private int foliageOverlay;
        private float foliageSaturationMultiplier;
        private int birchColor;
        private int spruceColor;

        TropicalSeason(int grassColour, float grassSaturation, int foliageColour, float foliageSaturation, int birchColor, int spruceColor) {
            this.grassOverlay = grassColour;
            this.grassSaturationMultiplier = grassSaturation;
            this.foliageOverlay = foliageColour;
            this.foliageSaturationMultiplier = foliageSaturation;
            this.birchColor = birchColor;
            this.spruceColor = spruceColor;
        }

        TropicalSeason(int grassColour, int foliageColour, int birchColor, int spruceColor) {
            this(grassColour, -1, foliageColour, -1, birchColor, spruceColor);
        }

        public int getGrassOverlay() {
            return this.grassOverlay;
        }

        public float getGrassSaturationMultiplier() {
            return this.grassSaturationMultiplier;
        }

        public int getFoliageOverlay() {
            return this.foliageOverlay;
        }

        public float getFoliageSaturationMultiplier() {
            return this.foliageSaturationMultiplier;
        }

        public int getBirchColor() {
            return this.birchColor;
        }

        public int getSpruceColor() {
            return this.spruceColor;
        }
    }
}
