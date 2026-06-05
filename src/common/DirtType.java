package common;

/**
 * ORTAK DOSYA — Kir türlerini temsil eden enum.
 * Model (Dirt sınıfları), View (renk), Controller (mantık) hepsi bunu kullanır.
 */
public enum DirtType {

    DUST(
            "Toz",
            Constants.DUST_CLEAN_TICKS,
            Constants.DUST_BATTERY_COST,
            Constants.COLOR_DIRT_DUST
    ),
    LIQUID(
            "Sıvı",
            Constants.LIQUID_CLEAN_TICKS,
            Constants.LIQUID_BATTERY_COST,
            Constants.COLOR_DIRT_LIQUID
    ),
    STAIN(
            "Leke",
            Constants.STAIN_CLEAN_TICKS,
            Constants.STAIN_BATTERY_COST,
            Constants.COLOR_DIRT_STAIN
    );

    private final String displayName;   // UI'da gösterilecek Türkçe ad
    private final int    cleanTicks;    // kaç tick'te temizlenir
    private final int    batteryCost;   // temizlerken harcanan batarya
    private final String color;         // JavaFX renk kodu

    DirtType(String displayName, int cleanTicks, int batteryCost, String color) {
        this.displayName = displayName;
        this.cleanTicks  = cleanTicks;
        this.batteryCost = batteryCost;
        this.color       = color;
    }

    public String getDisplayName() { return displayName; }
    public int    getCleanTicks()  { return cleanTicks;  }
    public int    getBatteryCost() { return batteryCost; }
    public String getColor()       { return color;       }
}
