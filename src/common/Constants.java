package common;

/**
 * ORTAK DOSYA — Üç kişi de bu dosyayı kullanır.
 * Değişiklik yapmadan önce GRUP TOPLANTISI yapılmalı!
 */
public final class Constants {

    private Constants() {} // instantiation engellendi

    // ─────────────────────────────────────────
    //  GRID / ODA
    // ─────────────────────────────────────────
    public static final int GRID_ROWS        = 15;
    public static final int GRID_COLS        = 20;
    public static final int CELL_SIZE        = 40;   // piksel
    public static final int CANVAS_WIDTH     = GRID_COLS * CELL_SIZE;  // 800
    public static final int CANVAS_HEIGHT    = GRID_ROWS * CELL_SIZE;  // 600

    // ─────────────────────────────────────────
    //  ROBOT
    // ─────────────────────────────────────────
    public static final int    MAX_BATTERY           = 100;
    public static final int    LOW_BATTERY_THRESHOLD = 20;
    public static final int    BATTERY_PER_MOVE      = 1;    // her harekette düşen
    public static final int    CHARGING_RATE         = 5;    // her tick'te şarj miktarı
    public static final double DEFAULT_SPEED         = 1.0;  // 1x
    public static final double MIN_SPEED             = 0.5;
    public static final double MAX_SPEED             = 3.0;

    // Şarj istasyonu başlangıç konumu
    public static final int CHARGING_STATION_X = 0;
    public static final int CHARGING_STATION_Y = 0;

    // ─────────────────────────────────────────
    //  KİR TÜRLERİ — Temizleme süreleri (tick)
    // ─────────────────────────────────────────
    public static final int DUST_CLEAN_TICKS   = 1;
    public static final int LIQUID_CLEAN_TICKS = 3;
    public static final int STAIN_CLEAN_TICKS  = 5;

    // Kir temizleme ek batarya tüketimi
    public static final int DUST_BATTERY_COST   = 1;
    public static final int LIQUID_BATTERY_COST = 3;
    public static final int STAIN_BATTERY_COST  = 5;

    // ─────────────────────────────────────────
    //  RENK PALETİ (JavaFX CSS string formatı)
    // ─────────────────────────────────────────
    public static final String COLOR_BACKGROUND      = "#F5F5F0";
    public static final String COLOR_CELL_EMPTY      = "#FFFFFF";
    public static final String COLOR_CELL_CLEANED    = "#E8F5E9";
    public static final String COLOR_CELL_OBSTACLE   = "#546E7A";
    public static final String COLOR_GRID_LINE       = "#BDBDBD";

    public static final String COLOR_DIRT_DUST       = "#D4A843";  // sarı-kahve
    public static final String COLOR_DIRT_LIQUID     = "#2196F3";  // mavi
    public static final String COLOR_DIRT_STAIN      = "#6D4C41";  // koyu kahve

    public static final String COLOR_ROBOT           = "#37474F";
    public static final String COLOR_ROBOT_OUTLINE   = "#90A4AE";
    public static final String COLOR_PATH            = "#80CBC4";  // geçilen yol izi
    public static final String COLOR_CHARGING_STATION = "#FFC107"; // sarı

    // Batarya rengi (yüzdeye göre Controller belirler, sabitler burada)
    public static final String COLOR_BATTERY_HIGH    = "#4CAF50";  // yeşil
    public static final String COLOR_BATTERY_MEDIUM  = "#FF9800";  // turuncu
    public static final String COLOR_BATTERY_LOW     = "#F44336";  // kırmızı

    // ─────────────────────────────────────────
    //  TEMİZLEME ALGORİTMALARI
    // ─────────────────────────────────────────
    public static final String ALGO_RANDOM     = "RANDOM";
    public static final String ALGO_SPIRAL     = "SPIRAL";
    public static final String ALGO_WALL_FOLLOW = "WALL_FOLLOW";

    // ─────────────────────────────────────────
    //  YÖN SABİTLERİ
    // ─────────────────────────────────────────
    public static final String DIR_NORTH = "NORTH";
    public static final String DIR_SOUTH = "SOUTH";
    public static final String DIR_EAST  = "EAST";
    public static final String DIR_WEST  = "WEST";

    // Yön vektörleri: [dx, dy] — NORTH = y azalır (grid koordinatı)
    public static final int[] DX = { 0,  0,  1, -1 }; // NORTH, SOUTH, EAST, WEST
    public static final int[] DY = {-1,  1,  0,  0 };
    public static final String[] DIRECTIONS = { DIR_NORTH, DIR_SOUTH, DIR_EAST, DIR_WEST };

    // ─────────────────────────────────────────
    //  SİMÜLASYON
    // ─────────────────────────────────────────
    public static final int    SIMULATION_TICK_MS = 500; // base tick süresi (ms)
    public static final int    MAX_PATH_STEPS     = 1000; // BFS max adım

    // ─────────────────────────────────────────
    //  UI BOYUTLARI
    // ─────────────────────────────────────────
    public static final double WINDOW_WIDTH       = 1100;
    public static final double WINDOW_HEIGHT      = 750;
    public static final double CONTROL_PANEL_WIDTH = 220;
    public static final double STATUS_BAR_HEIGHT   = 50;
}
