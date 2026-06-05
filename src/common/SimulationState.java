package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ORTAK DOSYA — Simülasyonun anlık durumunu tutar.
 *
 * Controller → günceller (update metodu)
 * View       → sadece okur (getter'lar)
 * Model      → doğrudan erişmez, Controller aracılığıyla yansıtılır
 *
 * Bu sınıf bir "snapshot" görevi görür; View her tick'te bunu okuyarak
 * kendini yeniler. Observer pattern ile birlikte çalışır.
 */
public class SimulationState {

    // ── Robot bilgileri ──────────────────────────────────────
    private int       robotX;
    private int       robotY;
    private int       battery;
    private Direction direction;
    private boolean   isCharging;

    // ── Oda / temizlik istatistikleri ────────────────────────
    private int totalCells;       // toplam yürünebilir hücre
    private int cleanedCells;     // temizlenen hücre sayısı
    private int dirtyCells;       // kirli hücre sayısı

    // ── Simülasyon kontrol ───────────────────────────────────
    private boolean isRunning;
    private boolean isPaused;
    private int     elapsedSeconds;
    private String  statusMessage;   // "Temizliyor", "Şarja Dönüyor" vb.
    private String  activeAlgorithm;

    // ── Robot hareket izi (View'ın yolu çizmesi için) ────────
    private List<int[]> movementPath;

    // ── Constructor ─────────────────────────────────────────
    public SimulationState() {
        this.robotX          = Constants.CHARGING_STATION_X;
        this.robotY          = Constants.CHARGING_STATION_Y;
        this.battery         = Constants.MAX_BATTERY;
        this.direction       = Direction.EAST;
        this.isCharging      = false;
        this.totalCells      = 0;
        this.cleanedCells    = 0;
        this.dirtyCells      = 0;
        this.isRunning       = false;
        this.isPaused        = false;
        this.elapsedSeconds  = 0;
        this.statusMessage   = "Hazır";
        this.activeAlgorithm = Constants.ALGO_RANDOM;
        this.movementPath    = new ArrayList<>();
    }

    // ── Controller tarafından çağrılan güncelleme metodu ─────
    /**
     * Her simülasyon tick'inde Controller bu metodu çağırır.
     * View bu metodu çağırmaz, sadece getter'ları kullanır.
     */
    public void update(
            int robotX, int robotY, int battery, Direction direction,
            boolean isCharging, int totalCells, int cleanedCells, int dirtyCells,
            boolean isRunning, boolean isPaused, int elapsedSeconds,
            String statusMessage, String activeAlgorithm, List<int[]> movementPath
    ) {
        this.robotX          = robotX;
        this.robotY          = robotY;
        this.battery         = battery;
        this.direction       = direction;
        this.isCharging      = isCharging;
        this.totalCells      = totalCells;
        this.cleanedCells    = cleanedCells;
        this.dirtyCells      = dirtyCells;
        this.isRunning       = isRunning;
        this.isPaused        = isPaused;
        this.elapsedSeconds  = elapsedSeconds;
        this.statusMessage   = statusMessage;
        this.activeAlgorithm = activeAlgorithm;
        this.movementPath    = new ArrayList<>(movementPath);
    }

    // ── Hesaplanan değerler ──────────────────────────────────
    /** Temizlenen alan yüzdesi: 0.0 - 100.0 */
    public double getCleanedPercentage() {
        if (totalCells == 0) return 0.0;
        return (cleanedCells * 100.0) / totalCells;
    }

    /** Kalan kirli alan yüzdesi */
    public double getDirtyPercentage() {
        if (totalCells == 0) return 0.0;
        return (dirtyCells * 100.0) / totalCells;
    }

    /** Geçen süreyi "MM:SS" formatında döndürür */
    public String getFormattedTime() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /** Batarya durumuna göre renk kodu döndürür */
    public String getBatteryColor() {
        if (battery > 50) return Constants.COLOR_BATTERY_HIGH;
        if (battery > 20) return Constants.COLOR_BATTERY_MEDIUM;
        return Constants.COLOR_BATTERY_LOW;
    }

    /** Hareket izinin değiştirilemez kopyasını döndürür */
    public List<int[]> getMovementPath() {
        return Collections.unmodifiableList(movementPath);
    }

    // ── Getter'lar (View sadece bunları kullanır) ─────────────
    public int       getRobotX()          { return robotX; }
    public int       getRobotY()          { return robotY; }
    public int       getBattery()         { return battery; }
    public Direction getDirection()       { return direction; }
    public boolean   isCharging()         { return isCharging; }
    public int       getTotalCells()      { return totalCells; }
    public int       getCleanedCells()    { return cleanedCells; }
    public int       getDirtyCells()      { return dirtyCells; }
    public boolean   isRunning()          { return isRunning; }
    public boolean   isPaused()           { return isPaused; }
    public int       getElapsedSeconds()  { return elapsedSeconds; }
    public String    getStatusMessage()   { return statusMessage; }
    public String    getActiveAlgorithm() { return activeAlgorithm; }
}
