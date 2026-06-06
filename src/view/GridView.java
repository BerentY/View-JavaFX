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

// YENİ: Animasyon için gerekli JavaFX kütüphaneleri
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GridView extends Canvas implements Interfaces.SimulationObserver {

    private final Interfaces.IRoom room;
    private final Interfaces.ISimulationController controller;
    private final ControlPanel controlPanel;
    private SimulationState lastState;

    // YENİ: Animasyon durumu için değişkenler
    private double animationRadius = 0;
    private int animatedCellX = -1;
    private int animatedCellY = -1;
    private Timeline animationTimeline;

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
            int gridX = (int) (event.getX() / Constants.CELL_SIZE);
            int gridY = (int) (event.getY() / Constants.CELL_SIZE);

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

        // YENİ: 3. KATMAN: Temizleme Animasyonu (Eğer Aktifse)
        if (animatedCellX != -1 && animatedCellY != -1 && animationRadius > 0) {
            // Yarı saydam beyaz bir "silgi" efekti
            gc.setFill(Color.rgb(255, 255, 255, 0.7));

            // Hücrenin tam orta piksel koordinatlarını buluyoruz
            double centerX = animatedCellX * Constants.CELL_SIZE + (Constants.CELL_SIZE / 2.0);
            double centerY = animatedCellY * Constants.CELL_SIZE + (Constants.CELL_SIZE / 2.0);

            // Merkezden dışa doğru küçülen daireyi çizdiriyoruz
            gc.fillOval(centerX - animationRadius, centerY - animationRadius, animationRadius * 2, animationRadius * 2);
        }

        // Eğer simülasyon aktifse ve elimizde bir durum snapshot'ı varsa hareketli ögeleri çiz
        if (state != null) {
            // 4. KATMAN: Geçilen yolların rota çizgisini çiz
            RobotDrawer.drawPath(gc, state.getMovementPath());

            // 5. KATMAN: Robotu en üst katmana konumlandırıp çiz
            RobotDrawer.drawRobot(gc, state.getRobotX(), state.getRobotY(), state.getDirection(), state.getBattery());
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  OBSERVER METOTLARI (Arayüz güncellemeleri Platform.runLater içinde olmalı)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onStateChanged(SimulationState state) {
        this.lastState = state;
        Platform.runLater(() -> redrawUI(state));
    }

    @Override
    public void onRobotMoved(int newX, int newY, Direction direction) {
        // En güncel durum snapshot'ı ile arayüzü tazele
        Platform.runLater(() -> redrawUI(lastState));
    }

    @Override
    public void onCellCleaned(int x, int y, DirtType dirtType) {
        Platform.runLater(() -> {
            // YENİ: Animasyon Tetikleyicisi

            // Eğer halihazırda yürütülen bir silme efekti varsa, onu durdur ki yenisiyle çakışmasın
            if (animationTimeline != null) {
                animationTimeline.stop();
            }

            // Temizlenen hücrenin koordinatlarını hafızaya al
            this.animatedCellX = x;
            this.animatedCellY = y;
            // Daireyi maksimum boyuttan (hücrenin yarısı kadar bir yarıçap) başlat
            this.animationRadius = Constants.CELL_SIZE / 2.0;

            // Timeline ile her 30 milisaniyede bir daireyi küçültecek döngü oluştur
            animationTimeline = new Timeline(new KeyFrame(Duration.millis(30), event -> {
                animationRadius -= 2.0; // Daireyi adım adım küçült

                redrawUI(lastState); // Ekranı yeni daire boyutuyla tekrar çiz

                // Animasyon tamamen bitip daire yok olduğunda temizlik yap
                if (animationRadius <= 0) {
                    animationTimeline.stop();
                    animatedCellX = -1;
                    animatedCellY = -1;
                    redrawUI(lastState); // Son bir kez temiz ekranda çizim yap
                }
            }));

            animationTimeline.setCycleCount(Timeline.INDEFINITE);
            animationTimeline.play();
        });
    }

    @Override
    public void onSimulationReset() {
        this.lastState = null;
        Platform.runLater(() -> {
            // Sıfırlama anında animasyon açıksa hemen durdur
            if (animationTimeline != null) {
                animationTimeline.stop();
                animatedCellX = -1;
                animatedCellY = -1;
            }
            redrawUI(null);
        });
    }

    // ─── Kullanmadığımız Observer Metotları (Gövdesi boş) ───
    @Override public void onBatteryLow(int batteryLevel) {}
    @Override public void onRobotReachedStation() {}
    @Override public void onChargingComplete() {}
    @Override public void onSimulationStarted() {}
    @Override public void onSimulationPaused() {}
    @Override public void onCleaningComplete(int totalSeconds) {}
}