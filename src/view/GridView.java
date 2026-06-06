package view;

import common.Constants;
import common.Direction;
import common.DirtType;
import common.Interfaces;
import common.SimulationState;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;

public class GridView extends Canvas implements Interfaces.SimulationObserver {

    private final Interfaces.IRoom room;
    private final Interfaces.ISimulationController controller;
    private final ControlPanel controlPanel;
    private SimulationState lastState;

    public GridView(Interfaces.IRoom room, Interfaces.ISimulationController controller, ControlPanel controlPanel) {
        super(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
        this.room = room;
        this.controller = controller;
        this.controlPanel = controlPanel;

        redrawUI(null); // İlk çizimi boş durumla başlat
        setupMouseInteraction();
    }

    /**
     * Fare tıklamalarını dinleyerek ControlPanel'den gelen seçime göre komut gönderir.
     */
    private void setupMouseInteraction() {
        this.setOnMouseClicked(event -> {
            // getDeltaX() yerine getX() olarak düzelttik
            int gridX = (int) (event.getX() / Constants.CELL_SIZE); //
            int gridY = (int) (event.getY() / Constants.CELL_SIZE); //

            // Geri kalan switch-case kodların tamamen aynı kalacak...
            // Sınırların dışına tıklanmadığından emin ol
            if (gridX >= 0 && gridX < Constants.GRID_COLS && gridY >= 0 && gridY < Constants.GRID_ROWS) {

                // SAĞ TIKLANDIYSA: Her zaman engeli/duvarı/kiri kaldırır
                if (event.getButton() == MouseButton.SECONDARY) {
                    controller.removeObstacle(gridX, gridY);
                }

                // SOL TIKLANDIYSA: Kontrol panelinde hangi araç seçiliyse onu ekler
                else if (event.getButton() == MouseButton.PRIMARY) {
                    String activeTool = controlPanel.getSelectedTool();

                    switch (activeTool) {
                        case "OBSTACLE":
                            controller.addObstacle(gridX, gridY);
                            break;
                        case "DUST":
                            controller.addDirt(gridX, gridY, DirtType.DUST);
                            break;
                        case "LIQUID":
                            controller.addDirt(gridX, gridY, DirtType.LIQUID);
                            break;
                        case "STAIN":
                            controller.addDirt(gridX, gridY, DirtType.STAIN);
                            break;
                    }
                }
            }
        });
    }

    /**
     * Tüm ekranı (Oda, Hücreler, Rota İzleri, Şarj İstasyonu ve Robot)
     * sırasıyla ve katmanlar halinde baştan çizen ana metot.
     */
    private void redrawUI(SimulationState state) {
        GraphicsContext gc = getGraphicsContext2D();

        // 1. KATMAN: Tüm hücreleri tek tek CellRenderer yardımıyla çiz
        for (int y = 0; y < room.getRows(); y++) {
            for (int x = 0; x < room.getCols(); x++) {
                Interfaces.ICell cell = room.getCell(x, y);
                CellRenderer.render(gc, cell); // Çizim tamamen delege edildi
            }
        }

        // 2. KATMAN: Şarj istasyonunu RobotDrawer yardımıyla çiz
        Interfaces.IChargingStation station = room.getChargingStation();
        RobotDrawer.drawChargingStation(gc, station.getX(), station.getY());

        // Eğer simülasyon aktifse ve elimizde bir durum snapshot'ı varsa hareketli ögeleri çiz
        if (state != null) {
            // 3. KATMAN: Geçilen yolların rota çizgisini çiz
            RobotDrawer.drawPath(gc, state.getMovementPath());

            // 4. KATMAN: Robotu en üst katmana konumlandırıp çiz
            RobotDrawer.drawRobot(gc, state.getRobotX(), state.getRobotY(), state.getDirection(), state.getBattery());
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  OBSERVER METOTLARI (Arayüz güncellemeleri Platform.runLater içinde olmalı)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onStateChanged(SimulationState state) {
        this.lastState = state;
        Platform.runLater(() -> redrawUI(state)); //
    }

    @Override
    public void onRobotMoved(int newX, int newY, Direction direction) {
        // En güncel durum snapshot'ı ile arayüzü tazele
        Platform.runLater(() -> redrawUI(lastState));
    }

    @Override
    public void onCellCleaned(int x, int y, DirtType dirtType) {
        // Hücre temizlendiğinde tüm katmanları senkronize tut
        Platform.runLater(() -> redrawUI(lastState));
    }

    @Override public void onSimulationReset() {
        this.lastState = null;
        Platform.runLater(() -> redrawUI(null)); //
    }

    @Override public void onBatteryLow(int batteryLevel) {}
    @Override public void onRobotReachedStation() {}
    @Override public void onChargingComplete() {}
    @Override public void onSimulationStarted() {}
    @Override public void onSimulationPaused() {}
    @Override public void onCleaningComplete(int totalSeconds) {}
}