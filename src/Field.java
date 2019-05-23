import java.awt.Point;
import java.util.ArrayList;

public class Field {

    /** Ширина шахматной доски */
    private static final int WIDTH = 8;

    /** Высота шахматной доски */
    private static final int HEIGHT = 8;

    /** Список занятых точек */
    private static ArrayList<Point> occupiedPoints = new ArrayList<>(); // список занятых клеток

    /** Массив со количеством ходов коня до каждой точки от начальной точки */
    private static int[][] pointsWithValues = new int[WIDTH][HEIGHT]; // инициализируем шахматное
                                                                      // поле

    public int[][] getPointsWithValues() {
        return pointsWithValues;
    }

    public void setPointsWithValues(final int x, final int y, final int value) {
        pointsWithValues[x][y] = value;
    }

    /**
     * @return список занятых точек
     */
    public static ArrayList<Point> getOccupiedPoints() {
        return occupiedPoints;
    }

    public void setOccupiedPoints(final Point point) {
        occupiedPoints.add(point);
    }

    public int getPointValue(final Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
        return pointsWithValues[x][y];
    }

    public static boolean isOccupied(final Point point) {
        for (Point p : occupiedPoints) {
            if (p.equals(point)) {
                return true;
            }
        }
        return false;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

}
