package common;

/**
 * ORTAK DOSYA — Simülasyon içinde gerçekleşen olayları temsil eden enum.
 *
 * Controller olayları fırlatır, View olayları dinler.
 * Observer metotlarına ek olarak, event loglamak veya debug için kullanılabilir.
 */
public enum SimulationEvent {

    SIMULATION_STARTED    ("Simülasyon başladı"),
    SIMULATION_PAUSED     ("Simülasyon duraklatıldı"),
    SIMULATION_RESUMED    ("Simülasyon devam ediyor"),
    SIMULATION_RESET      ("Simülasyon sıfırlandı"),
    SIMULATION_COMPLETED  ("Temizlik tamamlandı"),

    ROBOT_MOVED           ("Robot hareket etti"),
    ROBOT_TURNED          ("Robot yön değiştirdi"),
    ROBOT_HIT_WALL        ("Robot duvara çarptı"),
    ROBOT_HIT_OBSTACLE    ("Robot engele çarptı"),

    CELL_CLEANED          ("Hücre temizlendi"),
    DIRT_ADDED            ("Kir eklendi"),
    OBSTACLE_ADDED        ("Engel eklendi"),
    OBSTACLE_REMOVED      ("Engel kaldırıldı"),

    BATTERY_LOW           ("Batarya düşük"),
    BATTERY_CRITICAL      ("Batarya kritik"),
    BATTERY_DEPLETED      ("Batarya bitti"),
    CHARGING_STARTED      ("Şarj başladı"),
    CHARGING_COMPLETED    ("Şarj tamamlandı"),

    RETURNING_TO_STATION  ("İstasyona dönüyor"),
    REACHED_STATION       ("İstasyona ulaştı"),

    ALGORITHM_CHANGED     ("Algoritma değiştirildi"),
    SPEED_CHANGED         ("Hız değiştirildi");

    private final String displayMessage;

    SimulationEvent(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }
}
