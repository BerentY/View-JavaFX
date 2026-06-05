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
import javafx.scene.paint.Color;

public class GridView extends Canvas implements Interfaces.SimulationObserver {

    private final Interfaces.IRoom room;
    private final Interfaces.ISimulationController controller;
    private final ControlPanel controlPanel; // YENİ: Seçili aracı sorgulamak için eklendi
    private SimulationState lastState;

    // CONSTRUCTOR GÜNCELLENDİ: Artık ControlPanel referansını da parametre olarak alıyor
    public GridView(Interfaces.IRoom room, Interfaces.ISimulationController controller, ControlPanel controlPanel) {
        // Canvas boyutlarını Constants'tan alıyoruz
        super(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
        this.room = room;
        this.controller = controller;
        this.controlPanel = controlPanel; // Atama yapıldı

        drawInitialRoom();
        setupMouseInteraction();
    }

    /**
     * Fare tıklamalarını dinleyerek ControlPanel'den gelen seçime göre komut gönderir.
     */
    private void setupMouseInteraction() {
        this.setOnMouseClicked(event -> {
            // Tıklanan piksel koordinatını Grid hücresi (x, y) indeksine çevir
            int gridX = (int) (event.getX() / Constants.CELL_SIZE);
            int gridY = (int) (event.getY() / Constants.CELL_SIZE);

            // Sınırların dışına tıklanmadığından emin ol
            if (gridX >= 0 && gridX < Constants.GRID_COLS && gridY >= 0 && gridY < Constants.GRID_ROWS) {

                // SAĞ TIKLANDIYSA: Her zaman engeli/duvarı kaldırır
                if (event.getButton() == MouseButton.SECONDARY) {
                    controller.removeObstacle(gridX, gridY); //
                }

                // SOL TIKLANDIYSA: Kontrol panelinde hangi araç seçiliyse onu ekler
                else if (event.getButton() == MouseButton.PRIMARY) {
                    String activeTool = controlPanel.getSelectedTool();

                    switch (activeTool) {
                        case "OBSTACLE":
                            controller.addObstacle(gridX, gridY); //
                            break;
                        case "DUST":
                            controller.addDirt(gridX, gridY, DirtType.DUST); //
                            break;
                        case "LIQUID":
                            controller.addDirt(gridX, gridY, DirtType.LIQUID); //
                            break;
                        case "STAIN":
                            controller.addDirt(gridX, gridY, DirtType.STAIN); //
                            break;
                    }
                }
            }
        });
    }

    /**
     * Odanın mevcut halini, engelleri ve kirleri çizer.
     */
    private void drawInitialRoom() {
        GraphicsContext gc = getGraphicsContext2D();

        // Arka planı temizle (Boş hücre rengi)
        gc.setFill(Color.web(Constants.COLOR_CELL_EMPTY));
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Grid çizgilerini çiz
        gc.setStroke(Color.web(Constants.COLOR_GRID_LINE));
        gc.setLineWidth(1.0);

        for (int y = 0; y < room.getRows(); y++) {
            for (int x = 0; x < room.getCols(); x++) {
                Interfaces.ICell cell = room.getCell(x, y);

                double drawX = x * Constants.CELL_SIZE;
                double drawY = y * Constants.CELL_SIZE;

                // Hücre içeriğini çiz (Engel veya Kir)
                if (cell.isObstacle()) {
                    gc.setFill(Color.web(Constants.COLOR_CELL_OBSTACLE));
                    gc.fillRect(drawX, drawY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                } else if (cell.hasDirt()) {
                    gc.setFill(Color.web(cell.getDirtType().getColor()));
                    gc.fillRect(drawX, drawY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                } else if (cell.isCleaned()) {
                    // Gezilmiş/Temizlenmiş hücre rengini çiz (Görsel bütünlük için)
                    gc.setFill(Color.web(Constants.COLOR_CELL_CLEANED));
                    gc.fillRect(drawX, drawY, Constants.CELL_SIZE, Constants.CELL_SIZE);
                }

                // Hücre çerçevesini çiz
                gc.strokeRect(drawX, drawY, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }

        // Şarj istasyonunu çiz
        Interfaces.IChargingStation station = room.getChargingStation();
        gc.setFill(Color.web(Constants.COLOR_CHARGING_STATION));
        gc.fillRect(station.getX() * Constants.CELL_SIZE, station.getY() * Constants.CELL_SIZE, Constants.CELL_SIZE, Constants.CELL_SIZE);
    }

    /**
     * Robotu mevcut konumuna ve yönüne göre çizer.
     */
    private void drawRobot(int x, int y, Direction direction) {
        GraphicsContext gc = getGraphicsContext2D();
        double drawX = x * Constants.CELL_SIZE;
        double drawY = y * Constants.CELL_SIZE;
        double padding = 5.0; // Robotun hücre içindeki boşluğu
        double robotSize = Constants.CELL_SIZE - (padding * 2);

        // Robotun gövdesi
        gc.setFill(Color.web(Constants.COLOR_ROBOT));
        gc.fillOval(drawX + padding, drawY + padding, robotSize, robotSize);

        // Robotun yönünü belli eden bir işaret
        gc.setStroke(Color.web(Constants.COLOR_ROBOT_OUTLINE));
        gc.setLineWidth(3.0);

        double centerX = drawX + (Constants.CELL_SIZE / 2.0);
        double centerY = drawY + (Constants.CELL_SIZE / 2.0);
        double lineLength = robotSize / 2.0;

        // Yöne göre çizgiyi çiz (Kuzey, Güney, Doğu, Batı)
        gc.strokeLine(centerX, centerY, centerX + (direction.getDx() * lineLength), centerY + (direction.getDy() * lineLength));
    }

    // ═══════════════════════════════════════════════════════════
    //  OBSERVER METOTLARI (Arayüz güncellemeleri Platform.runLater içinde olmalı)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onStateChanged(SimulationState state) {
        this.lastState = state;
        Platform.runLater(() -> {
            drawInitialRoom();
            drawRobot(state.getRobotX(), state.getRobotY(), state.getDirection());
        });
    }

    @Override
    public void onRobotMoved(int newX, int newY, Direction direction) {
        Platform.runLater(() -> {
            drawInitialRoom();
            drawRobot(newX, newY, direction);
        });
    }

    @Override
    public void onCellCleaned(int x, int y, DirtType dirtType) {
        Platform.runLater(() -> {
            drawInitialRoom(); // Senkronizasyon kaybını önlemek için odayı yenile
            if (lastState != null) {
                drawRobot(lastState.getRobotX(), lastState.getRobotY(), lastState.getDirection());
            }
        });
    }

    @Override public void onBatteryLow(int batteryLevel) {}
    @Override public void onRobotReachedStation() {}
    @Override public void onChargingComplete() {}
    @Override public void onSimulationStarted() {}
    @Override public void onSimulationPaused() {}

    @Override
    public void onSimulationReset() {
        Platform.runLater(this::drawInitialRoom);
    }

    @Override
    public void onCleaningComplete(int totalSeconds) {}
}