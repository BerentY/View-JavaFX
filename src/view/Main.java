package view;

import common.Constants;
import common.Interfaces;
import common.DirtType;
import common.SimulationState;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Takım arkadaşlarının Controller ve Model sınıfları olmadığı için geçici test nesneleri oluşturuyoruz
        Interfaces.IRoom mockRoom = createMockRoom();
        Interfaces.ISimulationController mockController = createMockController();

        // View katmanımızı bu test nesneleriyle başlatıyoruz
        MainView mainView = new MainView(mockRoom, mockController);

        // Sahne boyutlarını Constants dosyasından alıyoruz
        Scene scene = new Scene(mainView, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setTitle("Akıllı Robot Süpürge Simülasyonu - View Test");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ─── GEÇİCİ TEST YAPILARI (Derleme Hatası Almamak İçin) ───

    private Interfaces.IRoom createMockRoom() {
        return new Interfaces.IRoom() {
            @Override public int getRows() { return Constants.GRID_ROWS; }
            @Override public int getCols() { return Constants.GRID_COLS; }
            @Override public int getTotalWalkableCells() { return Constants.GRID_ROWS * Constants.GRID_COLS; }
            @Override public int getCleanedCellCount() { return 0; }
            @Override public int getDirtyCellCount() { return 0; }
            @Override public boolean isValidPosition(int x, int y) { return true; }
            @Override public boolean isWalkable(int x, int y) { return true; }

            @Override public Interfaces.ICell getCell(int x, int y) {
                return new Interfaces.ICell() {
                    @Override public int getX() { return x; }
                    @Override public int getY() { return y; }
                    @Override public boolean isObstacle() { return false; }
                    @Override public boolean hasDirt() { return false; }
                    @Override public boolean isCleaned() { return false; }
                    @Override public DirtType getDirtType() { return null; }
                    @Override public int getDirtProgress() { return 0; }
                    @Override public void setObstacle(boolean obs) {}
                    @Override public void setDirt(DirtType type) {}
                    @Override public void clearDirt() {}
                    @Override public void setCleaned(boolean cln) {}
                    @Override public void incrementDirtProgress() {}
                };
            }

            @Override public Interfaces.IChargingStation getChargingStation() {
                return new Interfaces.IChargingStation() {
                    @Override public int getX() { return Constants.CHARGING_STATION_X; }
                    @Override public int getY() { return Constants.CHARGING_STATION_Y; }
                    @Override public boolean isRobotPresent() { return true; }
                    @Override public void setRobotPresent(boolean present) {}
                };
            }

            @Override public void addDirt(int x, int y, DirtType type) {}
            @Override public void addObstacle(int x, int y) {}
            @Override public void removeObstacle(int x, int y) {}
            @Override public void reset() {}
        };
    }

    private Interfaces.ISimulationController createMockController() {
        return new Interfaces.ISimulationController() {
            @Override public void startSimulation() { System.out.println("Başlat tıklandı."); }
            @Override public void pauseSimulation() { System.out.println("Duraklat tıklandı."); }
            @Override public void resetSimulation() { System.out.println("Sıfırla tıklandı."); }
            @Override public void returnToStation() { System.out.println("İstasyona Dön tıklandı."); }
            @Override public void setAlgorithm(String algo) { System.out.println("Algoritma seçildi: " + algo); }
            @Override public void setSpeed(double speed) { System.out.println("Hız ayarlandı: " + speed); }
            @Override public void addObstacle(int x, int y) { System.out.println("Engel eklendi: " + x + ", " + y); }
            @Override public void removeObstacle(int x, int y) { System.out.println("Engel/Kir silindi: " + x + ", " + y); }
            @Override public void addDirt(int x, int y, DirtType type) { System.out.println("Kir eklendi: " + type.getDisplayName()); }
            @Override public void setBattery(int level) { System.out.println("Batarya manuel ayarlandı: %" + level); }
            @Override public void addObserver(Interfaces.SimulationObserver obs) {}
            @Override public void removeObserver(Interfaces.SimulationObserver obs) {}
            @Override public SimulationState getState() { return new SimulationState(); }
            @Override public boolean isRunning() { return false; }
            @Override public boolean isPaused() { return false; }
        };
    }
}