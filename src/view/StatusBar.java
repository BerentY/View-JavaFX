package view;

import common.Constants;
import common.DirtType;
import common.Interfaces;
import common.SimulationState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Ekranın alt kısmında yer alan durum çubuğu.
 * Batarya, süre ve temizlik yüzdesi gibi anlık bilgileri gösterir.
 */
public class StatusBar extends HBox implements Interfaces.SimulationObserver {

    private Label lblStatus;
    private Label lblTime;
    private Label lblBattery;
    private Label lblCleaned;

    public StatusBar() {
        setPrefHeight(Constants.STATUS_BAR_HEIGHT);
        setPadding(new Insets(10, 20, 10, 20));
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        // Üstüne ince bir gri çizgi ve hafif bir arka plan rengi ekliyoruz
        setStyle("-fx-background-color: #ECEFF1; -fx-border-color: #BDBDBD; -fx-border-width: 1 0 0 0;");

        initUI();
    }

    private void initUI() {
        lblStatus = new Label("Durum: Hazır");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #37474F;");

        lblTime = new Label("Süre: 00:00");
        lblTime.setStyle("-fx-font-weight: bold;");

        lblBattery = new Label("Batarya: %100");
        lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");

        lblCleaned = new Label("Temizlenen: %0.0");
        lblCleaned.setStyle("-fx-font-weight: bold;");

        // Durum etiketi sol tarafta, diğer istatistikler sağ tarafta kalsın diye
        // araya görünmez, genişleyebilen bir yay (spacer) ekliyoruz.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(lblStatus, spacer, lblTime, lblBattery, lblCleaned);
    }

    // ═══════════════════════════════════════════════════════════
    //  OBSERVER METOTLARI (Arayüz güncellemeleri Platform.runLater içinde olmalı)
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onStateChanged(SimulationState state) {
        Platform.runLater(() -> {
            lblStatus.setText("Durum: " + state.getStatusMessage());
            lblTime.setText("Süre: " + state.getFormattedTime());

            lblBattery.setText("Batarya: %" + state.getBattery());
            // Batarya rengini Controller'ın belirlediği SimulationState üzerinden alıyoruz
            lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + state.getBatteryColor() + ";");

            // Yüzdeyi virgülden sonra tek haneli gösterecek şekilde formatlıyoruz
            lblCleaned.setText(String.format("Temizlenen: %%%.1f", state.getCleanedPercentage()));
        });
    }

    @Override
    public void onBatteryLow(int batteryLevel) {
        Platform.runLater(() -> lblStatus.setText("Durum: Batarya Düşük!"));
    }

    @Override
    public void onRobotReachedStation() {
        Platform.runLater(() -> lblStatus.setText("Durum: İstasyona Ulaşıldı"));
    }

    @Override
    public void onChargingComplete() {
        Platform.runLater(() -> lblStatus.setText("Durum: Şarj Tamamlandı"));
    }

    @Override
    public void onCleaningComplete(int totalSeconds) {
        Platform.runLater(() -> lblStatus.setText("Durum: Temizlik Bitti!"));
    }

    // ─── Kullanmadığımız Observer Metotları (Gövdesi boş) ───
    @Override public void onRobotMoved(int newX, int newY, common.Direction direction) {}
    @Override public void onCellCleaned(int x, int y, DirtType dirtType) {}
    @Override public void onSimulationStarted() {}
    @Override public void onSimulationPaused() {}
    @Override public void onSimulationReset() {}
}