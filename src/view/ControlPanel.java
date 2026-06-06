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
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

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

    // GÜNCELLEME: Hız ayarı artık ComboBox değil, Slider
    private Slider sliderSpeed;
    private Label lblSpeedValue;

    // YENİ: Robot Durumu Gösterge Etiketleri
    private Label lblRobotPos;
    private Label lblRobotDir;
    private Label lblRobotBattery;

    // YENİ: Manuel Batarya Ayarlama Elemanları
    private ComboBox<Integer> comboManualBattery;
    private Button btnSetBattery;

    // Hücreye sol tıklandığında ne ekleneceğini seçen araçlar
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
        btnPause.setDisable(true);
        btnPause.setOnAction(e -> controller.pauseSimulation());

        btnReset = new Button("Sıfırla");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setOnAction(e -> controller.resetSimulation());

        btnReturnStation = new Button("İstasyona Dön");
        btnReturnStation.setMaxWidth(Double.MAX_VALUE);
        btnReturnStation.setOnAction(e -> controller.returnToStation());

        // ─── ARAÇLAR (KİR / MOBİLYA EKLEME) ───
        Label lblTools = new Label("Ekleme Aracı (Sol Tık)");
        lblTools.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        toolGroup = new ToggleGroup();

        rbObstacle = new RadioButton("Mobilya (Engel)");
        rbObstacle.setToggleGroup(toolGroup);
        rbObstacle.setSelected(true);

        rbDust = new RadioButton("Toz Kiri (Sarı)");
        rbDust.setToggleGroup(toolGroup);

        rbLiquid = new RadioButton("Sıvı Kiri (Mavi)");
        rbLiquid.setToggleGroup(toolGroup);

        rbStain = new RadioButton("Leke Kiri (Kahve)");
        rbStain.setToggleGroup(toolGroup);

        VBox toolBox = new VBox(8, rbObstacle, rbDust, rbLiquid, rbStain);
        toolBox.setAlignment(Pos.CENTER_LEFT);
        toolBox.setPadding(new Insets(0, 0, 0, 20));

        // ─── YENİ: ROBOT DURUMU GÖSTERGE PANELİ ───
        Label lblStatusTitle = new Label("Robot Durumu");
        lblStatusTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        lblRobotPos = new Label("Konum (x, y): (0, 0)");
        lblRobotDir = new Label("Yön: Doğu (→)");
        lblRobotBattery = new Label("Batarya: %100");
        lblRobotBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");

        VBox statusBox = new VBox(6, lblRobotPos, lblRobotDir, lblRobotBattery);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setPadding(new Insets(0, 0, 0, 20));

        // ─── AYARLAR ───
        Label lblSettings = new Label("Ayarlar");
        lblSettings.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblAlgo = new Label("Algoritma:");
        comboAlgorithm = new ComboBox<>();
        comboAlgorithm.getItems().addAll(Constants.ALGO_RANDOM, Constants.ALGO_SPIRAL, Constants.ALGO_WALL_FOLLOW);
        comboAlgorithm.setValue(Constants.ALGO_RANDOM);
        comboAlgorithm.setMaxWidth(Double.MAX_VALUE);
        comboAlgorithm.setOnAction(e -> controller.setAlgorithm(comboAlgorithm.getValue()));

        // GÜNCELLEME: ComboBox yerine Slider yapısı entegre edildi
        Label lblSpeedTitle = new Label("Robot Hızı:");
        lblSpeedValue = new Label(Constants.DEFAULT_SPEED + "x");
        lblSpeedValue.setStyle("-fx-font-weight: bold;");
        HBox speedLabelBox = new HBox(5, lblSpeedTitle, lblSpeedValue);

        sliderSpeed = new Slider(Constants.MIN_SPEED, Constants.MAX_SPEED, Constants.DEFAULT_SPEED);
        sliderSpeed.setShowTickMarks(true);
        sliderSpeed.setShowTickLabels(false);
        sliderSpeed.setMajorTickUnit(0.5);
        sliderSpeed.setBlockIncrement(0.5);

        // Kaydırıcı hareket ettikçe anlık olarak hızı Controller'a bildirir ve yazıyı günceller
        sliderSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedSpeed = Math.round(newValue.doubleValue() * 2.0) / 2.0; // 0.5 katlarına yuvarla
            sliderSpeed.setValue(roundedSpeed);
            lblSpeedValue.setText(roundedSpeed + "x");
            controller.setSpeed(roundedSpeed);
        });

        // YENİ: MANUEL BATARYA AYARLAMA BÖLÜMÜ
        Label lblManualBattery = new Label("Manuel Batarya:");
        comboManualBattery = new ComboBox<>();
        for (int i = 10; i <= 100; i += 10) {
            comboManualBattery.getItems().add(i);
        }
        comboManualBattery.setValue(100);

        btnSetBattery = new Button("Ayarla");
        btnSetBattery.setOnAction(e -> controller.setBattery(comboManualBattery.getValue()));

        HBox batterySetBox = new HBox(5, comboManualBattery, btnSetBattery);
        batterySetBox.setAlignment(Pos.CENTER_LEFT);

        // Tüm elemanları panele ekle
        getChildren().addAll(
                lblControls,
                btnStart, btnPause, btnReset, btnReturnStation,
                new Separator(),
                lblTools,
                toolBox,
                new Separator(),
                lblStatusTitle,
                statusBox,
                new Separator(),
                lblSettings,
                lblAlgo, comboAlgorithm,
                speedLabelBox, sliderSpeed,
                lblManualBattery, batterySetBox
        );
    }

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

            // GÜNCELLEME: Anlık olarak robotun konumunu, yönünü ve bataryasını arayüze yansıt
            lblRobotPos.setText(String.format("Konum (x, y): (%d, %d)", state.getRobotX(), state.getRobotY()));
            lblRobotDir.setText("Yön: " + state.getDirection().getDisplayName());
            lblRobotBattery.setText("Batarya: %" + state.getBattery());
            lblRobotBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + state.getBatteryColor() + ";");

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
            sliderSpeed.setValue(Constants.DEFAULT_SPEED);
            lblSpeedValue.setText(Constants.DEFAULT_SPEED + "x");
            comboManualBattery.setValue(100);
            rbObstacle.setSelected(true);

            // Başlangıç değerlerine geri döndür
            lblRobotPos.setText("Konum (x, y): (0, 0)");
            lblRobotDir.setText("Yön: Doğu (→)");
            lblRobotBattery.setText("Batarya: %100");
            lblRobotBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");
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