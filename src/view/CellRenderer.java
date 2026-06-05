package view;

import common.DirtType;
import common.Constants;
import common.Interfaces.ICell;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Görevi: Tek bir hücreyi Canvas üzerine çizmek.
 * Model katmanına bağımlılığı sıfırdır, sadece ICell kullanır.
 */
public class CellRenderer {

    public static void render(GraphicsContext gc, ICell cell) {
        int x = cell.getX() * Constants.CELL_SIZE;
        int y = cell.getY() * Constants.CELL_SIZE;
        int size = Constants.CELL_SIZE;

        // 1. Hücre tipine göre arka plan rengini belirle
        if (cell.isObstacle()) {
            gc.setFill(Color.web(Constants.COLOR_CELL_OBSTACLE));
        } else if (cell.hasDirt() && cell.getDirtType() != null) {
            gc.setFill(Color.web(cell.getDirtType().getColor()));
        } else if (cell.isCleaned()) {
            gc.setFill(Color.web(Constants.COLOR_CELL_CLEANED));
        } else {
            gc.setFill(Color.web(Constants.COLOR_CELL_EMPTY));
        }

        // Hücreyi doldur
        gc.fillRect(x, y, size, size);

        // 2. Grid çizgilerini çiz
        gc.setStroke(Color.web(Constants.COLOR_GRID_LINE));
        gc.setLineWidth(1.0);
        gc.strokeRect(x, y, size, size);
    }
}