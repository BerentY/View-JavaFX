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
// YENİ: Ses çalabilmek için Media kütüphanesini import ettik
import javafx.scene.media.AudioClip;

/**
 * Ekranın alt kısmında yer alan durum çubuğu.
 * Batarya, süre, alan istatistikleri ve temizlik yüzdesi gibi anlık bilgileri gösterir.
 * AYRICA: Projenin ses efektlerini (AudioClip) burada çalarak Bonus kuralını yerine getirir.
 */
public class StatusBar extends HBox implements Interfaces.SimulationObserver {

    private Label lblStatus;
    private Label lblTime;
    private Label lblBattery;
    private Label lblCleaned;

    // Ödev dokümanında zorunlu tutulan alan etiketleri
    private Label lblTotalArea;
    private Label lblRemainingArea;

    // YENİ: Ses çalma nesnelerimiz (AudioClip)
    private AudioClip cleanSound;
    private AudioClip chargeSound;
    private AudioClip completeSound;

    public StatusBar() {
        setPrefHeight(Constants.STATUS_BAR_HEIGHT);
        setPadding(new Insets(10, 20, 10, 20));
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        // Üstüne ince bir gri çizgi ve hafif bir arka plan rengi ekliyoruz
        setStyle("-fx-background-color: #ECEFF1; -fx-border-color: #BDBDBD; -fx-border-width: 1 0 0 0;");

        initUI();
        initSounds(); // YENİ: Arayüz çizildikten sonra ses dosyalarını yükle
    }

    private void initUI() {
        lblStatus = new Label("Durum: Hazır");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #37474F;");

        // Toplam alan göstergesi (Görsel taslakla tam uyumlu formatta)
        lblTotalArea = new Label("Toplam Alan: 0 m²");
        lblTotalArea.setStyle("-fx-font-weight: bold; -fx-text-fill: #455A64;");

        lblTime = new Label("Süre: 00:00");
        lblTime.setStyle("-fx-font-weight: bold;");

        lblBattery = new Label("Batarya: %100");
        lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");

        lblCleaned = new Label("Temizlenen: %0.0");
        lblCleaned.setStyle("-fx-font-weight: bold;");

        // Kalan kirli alan göstergesi
        lblRemainingArea = new Label("Kalan Alan: 0 m² (%100.0)");
        lblRemainingArea.setStyle("-fx-font-weight: bold; -fx-text-fill: #5D4037;");

        // Durum etiketleri sol tarafta, istatistikler sağ tarafta düzgünce hizalansın diye
        // araya genişleyebilen görünmez bir ayırıcı ekliyoruz.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Yeni etiketlerimizi de düzene dahil ediyoruz
        getChildren().addAll(lblStatus, lblTotalArea, spacer, lblTime, lblBattery, lblCleaned, lblRemainingArea);
    }

    /**
     * YENİ: Ses dosyalarını bilgisayardan okuyup belleğe yükler.
     * Constants sınıfını değiştirmemek için yolları buraya hardcode ettik.
     */
    private void initSounds() {
        try {
            // getClass().getResource() metodu src klasöründen itibaren dosyayı arar.
            cleanSound = new AudioClip(getClass().getResource("/sounds/clean.wav").toExternalForm());
            chargeSound = new AudioClip(getClass().getResource("/sounds/charge.wav").toExternalForm());
            completeSound = new AudioClip(getClass().getResource("/sounds/complete.wav").toExternalForm());
        } catch (Exception e) {
            // Eğer dosya ismi yanlışsa veya klasörde yoksa uygulama çökmez, sadece uyarı verir.
            System.err.println("UYARI: Ses dosyaları (clean.wav, charge.wav, complete.wav) '/sounds/' klasöründe bulunamadı.");
        }
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
            lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + state.getBatteryColor() + ";");

            // Temizlenen ve Kalan alan yüzdelerini anlık hesaplayarak yazdırıyoruz
            lblCleaned.setText(String.format("Temizlenen: %%%.1f", state.getCleanedPercentage()));

            // GÜNCELLEME: Toplam ve Kalan alan metriklerini canlı verilerle besle
            lblTotalArea.setText(String.format("Toplam Alan: %d m²", state.getTotalCells()));
            lblRemainingArea.setText(String.format("Kalan Alan: %d m² (%%%.1f)",
                    state.getDirtyCells(),
                    state.getDirtyPercentage()));
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
        Platform.runLater(() -> {
            lblStatus.setText("Durum: Şarj Tamamlandı");
            // YENİ: Şarj tamamlanınca tatlı bir melodi çal
            if (chargeSound != null) {
                chargeSound.play();
            }
        });
    }

    @Override
    public void onCleaningComplete(int totalSeconds) {
        Platform.runLater(() -> {
            lblStatus.setText("Durum: Temizlik Bitti!");
            // YENİ: Tüm oda temizlenince başarı sesini çal
            if (completeSound != null) {
                completeSound.play();
            }
        });
    }

    @Override
    public void onCellCleaned(int x, int y, DirtType dirtType) {
        // YENİ: Robot her kir sildiğinde kısa temizlik sesini çal
        Platform.runLater(() -> {
            if (cleanSound != null) {
                cleanSound.play();
            }
        });
    }

    @Override
    public void onSimulationReset() {
        Platform.runLater(() -> {
            lblStatus.setText("Durum: Hazır");
            lblTime.setText("Süre: 00:00");
            lblBattery.setText("Batarya: %100");
            lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");
            lblCleaned.setText("Temizlenen: %0.0");
            lblTotalArea.setText("Toplam Alan: 0 m²");
            lblRemainingArea.setText("Kalan Alan: 0 m² (%100.0)");
        });
    }

    // ─── Kullanmadığımız Observer Metotları (Gövdesi boş) ───
    @Override public void onRobotMoved(int newX, int newY, common.Direction direction) {}
    @Override public void onSimulationStarted() {}
    @Override public void onSimulationPaused() {}
}