package view;

import common.Constants;
import common.Interfaces;
import common.SimulationState;
import common.DirtType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * Kullanıcı kontrollerini barındıran sağ panel.
 * Controller'a komut gönderir ve Observer üzerinden kendi UI durumunu günceller.
 */
public class ControlPanel extends VBox implements Interfaces.SimulationObserver {

    private final Interfaces.ISimulationController controller;

    // Arayüz Elemanları
    private Button btnStart;
    private Button btnPause;
    private Button btnReset;
    private Button btnReturnStation;

    private ComboBox<String> comboAlgorithm;
    private ComboBox<Double> comboSpeed;

    // YENİ: Hücreye sol tıklandığında ne ekleneceğini seçen araçlar
    private ToggleGroup toolGroup;
    private RadioButton rbObstacle;
    private RadioButton rbDust;
    private RadioButton rbLiquid;
    private RadioButton rbStain;

    public ControlPanel(Interfaces.ISimulationController controller) {
        this.controller = controller;

        // VBox Ayarları
        setPrefWidth(Constants.CONTROL_PANEL_WIDTH);
        setPadding(new Insets(15));
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #E0E0E0; -fx-border-color: #BDBDBD; -fx-border-width: 0 0 0 1;");

        initUI();
    }

    private void initUI() {
        // ─── SİMÜLASYON KONTROLLERİ ───
        Label lblControls = new Label("Simülasyon Kontrolleri");
        lblControls.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        btnStart = new Button("Başlat");
        btnStart.setMaxWidth(Double.MAX_VALUE);
        btnStart.setOnAction(e -> controller.startSimulation());

        btnPause = new Button("Duraklat");
        btnPause.setMaxWidth(Double.MAX_VALUE);
        btnPause.setDisable(true); // Başlangıçta pasif
        btnPause.setOnAction(e -> controller.pauseSimulation());

        btnReset = new Button("Sıfırla");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setOnAction(e -> controller.resetSimulation());

        btnReturnStation = new Button("İstasyona Dön");
        btnReturnStation.setMaxWidth(Double.MAX_VALUE);
        btnReturnStation.setOnAction(e -> controller.returnToStation());

        // ─── YENİ: ARAÇLAR (KİR / MOBİLYA EKLEME) ───
        Label lblTools = new Label("Ekleme Aracı (Sol Tık)");
        lblTools.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        toolGroup = new ToggleGroup();

        rbObstacle = new RadioButton("Mobilya (Engel)");
        rbObstacle.setToggleGroup(toolGroup);
        rbObstacle.setSelected(true); // Varsayılan seçim

        rbDust = new RadioButton("Toz Kiri (Sarı)");
        rbDust.setToggleGroup(toolGroup);

        rbLiquid = new RadioButton("Sıvı Kiri (Mavi)");
        rbLiquid.setToggleGroup(toolGroup);

        rbStain = new RadioButton("Leke Kiri (Kahve)");
        rbStain.setToggleGroup(toolGroup);

        // Araçları dikey düzende hizalamak için küçük bir iç VBox
        VBox toolBox = new VBox(8, rbObstacle, rbDust, rbLiquid, rbStain);
        toolBox.setAlignment(Pos.CENTER_LEFT);
        toolBox.setPadding(new Insets(0, 0, 0, 20)); // Biraz içten başlat şık dursun

        // ─── AYARLAR ───
        Label lblSettings = new Label("Ayarlar");
        lblSettings.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblAlgo = new Label("Algoritma:");
        comboAlgorithm = new ComboBox<>();
        comboAlgorithm.getItems().addAll(Constants.ALGO_RANDOM, Constants.ALGO_SPIRAL, Constants.ALGO_WALL_FOLLOW);
        comboAlgorithm.setValue(Constants.ALGO_RANDOM);
        comboAlgorithm.setMaxWidth(Double.MAX_VALUE);
        comboAlgorithm.setOnAction(e -> controller.setAlgorithm(comboAlgorithm.getValue()));

        Label lblSpeed = new Label("Hız (Çarpan):");
        comboSpeed = new ComboBox<>();
        comboSpeed.getItems().addAll(Constants.MIN_SPEED, Constants.DEFAULT_SPEED, 2.0, Constants.MAX_SPEED);
        comboSpeed.setValue(Constants.DEFAULT_SPEED);
        comboSpeed.setMaxWidth(Double.MAX_VALUE);
        comboSpeed.setOnAction(e -> controller.setSpeed(comboSpeed.getValue()));

        // Tüm elemanları panele ekle
        getChildren().addAll(
                lblControls,
                btnStart, btnPause, btnReset, btnReturnStation,
                new Separator(),
                lblTools,
                toolBox, // Yeni ekleme araçları kutusu
                new Separator(),
                lblSettings,
                lblAlgo, comboAlgorithm,
                lblSpeed, comboSpeed
        );
    }

    /**
     * YENİ: GridView'ın sol tıklandığında hangi aracın aktif olduğunu
     * öğrenmesini sağlayan köprü metot.
     */
    public String getSelectedTool() {
        if (rbDust.isSelected()) return "DUST";
        if (rbLiquid.isSelected()) return "LIQUID";
        if (rbStain.isSelected()) return "STAIN";
        return "OBSTACLE";
    }

    // ═══════════════════════════════════════════════════════════
    //  OBSERVER METOTLARI (Arayüz güncellemeleri Platform.runLater içinde olmalı)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onStateChanged(SimulationState state) {
        Platform.runLater(() -> {
            boolean running = state.isRunning();
            boolean paused = state.isPaused();

            btnStart.setDisable(running && !paused);
            btnPause.setDisable(!running || paused);

            if (state.getActiveAlgorithm() != null && !comboAlgorithm.getValue().equals(state.getActiveAlgorithm())) {
                comboAlgorithm.setValue(state.getActiveAlgorithm());
            }
        });
    }

    @Override
    public void onSimulationStarted() {
        Platform.runLater(() -> {
            btnStart.setDisable(true);
            btnPause.setDisable(false);
        });
    }

    @Override
    public void onSimulationPaused() {
        Platform.runLater(() -> {
            btnStart.setDisable(false);
            btnPause.setDisable(true);
        });
    }

    @Override
    public void onSimulationReset() {
        Platform.runLater(() -> {
            btnStart.setDisable(false);
            btnPause.setDisable(true);
            comboAlgorithm.setValue(Constants.ALGO_RANDOM);
            comboSpeed.setValue(Constants.DEFAULT_SPEED);
            rbObstacle.setSelected(true); // Sıfırlanınca varsayılana dön
        });
    }

    @Override public void onRobotMoved(int newX, int newY, common.Direction direction) {}
    @Override public void onCellCleaned(int x, int y, DirtType dirtType) {}
    @Override public void onBatteryLow(int batteryLevel) {}
    @Override public void onRobotReachedStation() {}
    @Override public void onChargingComplete() {}

    @Override
    public void onCleaningComplete(int totalSeconds) {
        Platform.runLater(() -> {
            btnStart.setDisable(true);
            btnPause.setDisable(true);
        });
    }
}