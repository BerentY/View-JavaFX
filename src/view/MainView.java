package view;

import common.Interfaces.IRoom;
import common.Interfaces.ISimulationController;
import javafx.scene.layout.BorderPane;

/**
 * Görevi: Tüm sahneyi düzenler (BorderPane).
 * Merkez=GridView, Sağ=Control Panel, Alt=StatusBar olacak şekilde tasarlanır.
 */
public class MainView extends BorderPane {

    private GridView gridView;
    private ControlPanel controlPanel;
    private StatusBar statusBar;

    public MainView(IRoom room, ISimulationController controller) {

        // 1. ÖNCE ControlPanel'i oluşturuyoruz (Çünkü GridView bu panele danışacak)
        this.controlPanel = new ControlPanel(controller);

        // 2. SONRA GridView'ı oluşturup, içine hem odayı, hem controller'ı, hem de controlPanel'i gönderiyoruz
        this.gridView = new GridView(room, controller, this.controlPanel);

        // 3. StatusBar'ı oluşturuyoruz
        this.statusBar = new StatusBar();

        // 4. View bileşenlerimizi Controller'a "Observer" olarak kaydet
        controller.addObserver(gridView); //
        controller.addObserver(controlPanel); //
        controller.addObserver(statusBar); //

        // 5. BorderPane yerleşimi
        this.setCenter(gridView); //
        this.setRight(controlPanel); //
        this.setBottom(statusBar); //
    }
}