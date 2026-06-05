package common;

import java.util.List;

/**
 * ORTAK DOSYA — Tüm interface sözleşmeleri burada.
 *
 * Kişi 1 (Model): Bu interface'leri implement eder
 * Kişi 2 (View):  Bu interface'leri parametre olarak alır, içine bakmaz
 * Kişi 3 (Controller): Bu interface'leri bağlar
 *
 * KURAL: Interface imzası değiştirilmeden önce mutlaka grup toplantısı yapılır!
 */
public final class Interfaces {

    private Interfaces() {} // yardımcı sınıf, instantiation engellendi

    // ═══════════════════════════════════════════════════════════
    //  MODEL INTERFACE'LERİ
    // ═══════════════════════════════════════════════════════════

    /**
     * Robot'un View ve Controller'a sunduğu sözleşme.
     * Kişi 1: Robot.java bu interface'i implement eder.
     * Kişi 2 & 3: Sadece bu interface üzerinden Robot'a erişir.
     */
    public interface IRobot {
        int       getX();
        int       getY();
        int       getBattery();
        Direction getDirection();
        boolean   isBatteryLow();
        boolean   isCharging();
        double    getSpeed();
        List<int[]> getMovementPath();  // geçilen hücrelerin koordinatları

        // Controller'ın çağırdığı eylem metotları
        void move(int newX, int newY);
        void setDirection(Direction d);
        void decreaseBattery(int amount);
        void startCharging();
        void stopCharging();
        void chargeTick();       // her tick'te şarj artışı
        void setSpeed(double speed);
        void reset(int startX, int startY);
    }

    /**
     * Room'un View ve Controller'a sunduğu sözleşme.
     * Kişi 1: Room.java bu interface'i implement eder.
     */
    public interface IRoom {
        ICell getCell(int x, int y);
        int   getRows();
        int   getCols();
        IChargingStation getChargingStation();

        boolean isValidPosition(int x, int y);
        boolean isWalkable(int x, int y);

        int getTotalWalkableCells();
        int getCleanedCellCount();
        int getDirtyCellCount();

        void addDirt(int x, int y, DirtType type);
        void addObstacle(int x, int y);
        void removeObstacle(int x, int y);
        void reset();                       // tüm odayı başlangıç durumuna getir
    }

    /**
     * Hücrenin View ve Controller'a sunduğu sözleşme.
     * Kişi 1: Cell.java bu interface'i implement eder.
     */
    public interface ICell {
        int      getX();
        int      getY();
        boolean  isObstacle();
        boolean  isCleaned();
        boolean  hasDirt();
        DirtType getDirtType();   // dirt yoksa null
        int      getDirtProgress(); // kaç tick temizlendi (0 - cleanTicks)

        void setObstacle(boolean obstacle);
        void setDirt(DirtType type);
        void clearDirt();
        void setCleaned(boolean cleaned);
        void incrementDirtProgress(); // her temizleme tick'inde +1
    }

    /**
     * Şarj istasyonunun sözleşmesi.
     */
    public interface IChargingStation {
        int     getX();
        int     getY();
        boolean isRobotPresent();
        void    setRobotPresent(boolean present);
    }

    // ═══════════════════════════════════════════════════════════
    //  OBSERVER (OBSERVER PATTERN)
    // ═══════════════════════════════════════════════════════════

    /**
     * View'ın implement ettiği dinleyici.
     * Controller olaylar gerçekleştiğinde bu metotları çağırır.
     * Kişi 2: GridView ve ControlPanel bu interface'i implement eder.
     * Kişi 3: Controller bu interface'e event fırlatır.
     */
    public interface SimulationObserver {

        /** Her simülasyon tick'inde tüm durum güncellenir */
        void onStateChanged(SimulationState state);

        /** Robot yeni bir hücreye geçti */
        void onRobotMoved(int newX, int newY, Direction direction);

        /** Bir hücre temizlendi */
        void onCellCleaned(int x, int y, DirtType dirtType);

        /** Batarya kritik seviyeye düştü */
        void onBatteryLow(int batteryLevel);

        /** Robot şarj istasyonuna ulaştı */
        void onRobotReachedStation();

        /** Robot şarj tamamlandı */
        void onChargingComplete();

        /** Simülasyon başlatıldı */
        void onSimulationStarted();

        /** Simülasyon duraklatıldı */
        void onSimulationPaused();

        /** Simülasyon sıfırlandı */
        void onSimulationReset();

        /** Tüm alan temizlendi */
        void onCleaningComplete(int totalSeconds);
    }

    // ═══════════════════════════════════════════════════════════
    //  CONTROLLER INTERFACE'İ
    // ═══════════════════════════════════════════════════════════

    /**
     * View'ın Controller'a yapabileceği çağrılar.
     * Kişi 3: SimulationController bu interface'i implement eder.
     * Kişi 2: View butonlara basıldığında bu metotları çağırır.
     */
    public interface ISimulationController {

        // Simülasyon kontrolü
        void startSimulation();
        void pauseSimulation();
        void resetSimulation();
        void returnToStation();      // kullanıcı "İstasyona Dön" butonuna bastı

        // Kullanıcı eylemleri
        void addDirt(int gridX, int gridY, DirtType type);
        void addObstacle(int gridX, int gridY);
        void removeObstacle(int gridX, int gridY);
        void setSpeed(double speed);
        void setAlgorithm(String algorithmName);
        void setBattery(int level);  // kullanıcı manuel batarya ayarladı

        // Observer yönetimi
        void addObserver(SimulationObserver observer);
        void removeObserver(SimulationObserver observer);

        // Durum sorgulama (View butonları aktif/pasif etmek için kullanır)
        SimulationState getState();
        boolean isRunning();
        boolean isPaused();
    }

    // ═══════════════════════════════════════════════════════════
    //  PATHFINDER INTERFACE'İ
    // ═══════════════════════════════════════════════════════════

    /**
     * PathFinder algoritmaları için sözleşme.
     * Kişi 1: PathFinder.java bu interface'i implement eder.
     * Kişi 3: Controller bu interface üzerinden yol bulur.
     */
    public interface IPathFinder {
        /**
         * Başlangıç noktasından hedefe en kısa yolu döndürür.
         * @return koordinat listesi [[x1,y1],[x2,y2],...] başlangıç dahil değil.
         *         Yol bulunamazsa boş liste.
         */
        List<int[]> findPath(IRoom room, int startX, int startY, int goalX, int goalY);
    }
}
