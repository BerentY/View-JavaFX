package view;

import common.Constants;
import common.Interfaces.IRobot;
import common.Interfaces.IRoom;
import common.Interfaces.ISimulationController;
import controller.SimulationController;
import model.Robot;
import model.Room;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Görevi: JavaFX uygulamasını başlatmak ve tüm MVC parçalarını birleştirmek.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // 1. MODEL: Gerçek Oda ve Robot nesnelerini oluşturuyoruz
        IRoom room = new Room();
        IRobot robot = new Robot(Constants.CHARGING_STATION_X, Constants.CHARGING_STATION_Y);

        // 2. CONTROLLER: Kişi 3'ün yazdığı gerçek Controller'ı oluşturuyoruz
        // SimulationController, parametre olarak IRoom ve IRobot bekler.
        ISimulationController controller = new SimulationController(room, robot);

        // 3. VIEW: Gerçek odayı ve controller'ı arayüze paslıyoruz
        MainView mainView = new MainView(room, controller);

        // 4. Sahne Ayarları
        Scene scene = new Scene(mainView, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        primaryStage.setTitle("Robot Süpürge Simülasyonu — Tam Sürüm");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}