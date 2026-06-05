package view;

import common.Constants;
import common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

/**
 * Kişi 2 - Robot, Şarj İstasyonu ve Rota Çizici Yardımcı Sınıfı
 */
public class RobotDrawer {

    public static void drawRobot(GraphicsContext gc, int x, int y, Direction direction, int battery) {
        int px = x * Constants.CELL_SIZE;
        int py = y * Constants.CELL_SIZE;
        int size = Constants.CELL_SIZE;

        // Robot Dış Gövdesi
        gc.setFill(Color.web(Constants.COLOR_ROBOT));
        gc.fillOval(px + 4, py + 4, size - 8, size - 8);

        // Robot Çerçeve Çizgisi
        gc.setStroke(Color.web(Constants.COLOR_ROBOT_OUTLINE));
        gc.setLineWidth(2);
        gc.strokeOval(px + 4, py + 4, size - 8, size - 8);

        // Yön Gösterge Çizgisi (Merkezden baktığı yöne doğru)
        double centerX = px + size / 2.0;
        double centerY = py + size / 2.0;
        double radius = size / 3.5;

        double dx = direction.getDx() * radius;
        double dy = direction.getDy() * radius;

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(centerX, centerY, centerX + dx, centerY + dy);
    }

    public static void drawPath(GraphicsContext gc, List<int[]> path) {
        if (path == null || path.isEmpty()) return;

        gc.setStroke(Color.web(Constants.COLOR_PATH));
        gc.setLineWidth(2.5);

        // Geçilen tüm koordinatları çizgiyle birbirine bağla
        for (int i = 0; i < path.size() - 1; i++) {
            int[] current = path.get(i);
            int[] next = path.get(i + 1);

            double x1 = current[0] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double y1 = current[1] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double x2 = next[0] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
            double y2 = next[1] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    public static void drawChargingStation(GraphicsContext gc, int x, int y) {
        int px = x * Constants.CELL_SIZE;
        int py = y * Constants.CELL_SIZE;
        int size = Constants.CELL_SIZE;

        gc.setFill(Color.web(Constants.COLOR_CHARGING_STATION));
        gc.fillRect(px + 4, py + 4, size - 8, size - 8);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeRect(px + 10, py + 10, size - 20, size - 20);
    }
}