import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

/**
 * Нахождение кратчайшего пути шахматного коня.
 * 
 * @author Артур Исламгулов
 * @version 0.2b
 */

public class Chess {

    private static final Logger LOG = LogManager.getLogger(Chess.class);

    /** Путь к XML-файлу с аргументами */
    static String pathXML;

    /** Использовать XML-файл как аргумент (флаг --task) */
    static boolean isTask = false;

    /** Показать путь коня из начальной в конечную точки (флаг --show-path) */
    static boolean isShowPath = false;

    /** Начальная точка коня */
    static Point startPoint;

    /** Конечная точка коня */
    static Point finishPoint;

    /** Начальная шахматная координата коня */
    public static String coordinateOne;

    /** Конечная шахматная координата коня */
    public static String coordinateTwo;

    /** Минимальное количество ходов коня */
    private static int counts;

    /**
     * Производит обработку аргументов и вычисление минимального количества ходов коня между
     * заданными точками.
     * 
     * @param args аргументы запуска приложения
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws DocumentException
     */
    public static void main(String[] args)
            throws SAXException, ParserConfigurationException, DocumentException {
        StatusLogger.getLogger().setLevel(Level.OFF);

        Parser parser = new Parser();
        Field field = new Field();

        if (args.length == 2 || args.length == 3) {
            parser.parseArgs(args);
            if ((startPoint != null && finishPoint != null) || isTask) {
                if (isTask) {
                    parser.parseXML(pathXML);
                } else {
                    coordinateOne = Converter.convertPointToChessCoordinate(startPoint);
                    coordinateTwo = Converter.convertPointToChessCoordinate(finishPoint);
                }
                counts = countMoves(startPoint, finishPoint, field);
                LOG.info("Из клетки {} в клетку {} трeбуется ходов коня: {}.", coordinateOne,
                        coordinateTwo, counts);
                if (isShowPath) {
                    LOG.info("Кратчайший путь: {}",
                            printShowPath(startPoint, finishPoint, counts, field));
                }
            } else {
                LOG.error("Неверные аргументы.\nКоординаты [a-h][1-8]\nАргументы в XML: "
                        + "--task {name}.xml\nПоказать путь(опционально): --show-path");
            }
        } else {
            LOG.error("Неверное количество аргументов.");
        }
    }

    /**
     * Вычисляет и отдает кратчайший путь коня из точки в точку
     * 
     * @param startPoint
     * @param finishPoint
     * @param counts
     * @param field
     * @return кратчайший путь коня из startPoint в finishPoint
     */
    static String printShowPath(final Point startPoint, final Point finishPoint, final int counts,
            Field field) {
        ArrayList<String> pathList = new ArrayList<>();
        Point currentPoint = finishPoint;
        pathList.add(Converter.convertPointToChessCoordinate(finishPoint));
        int currentStep = counts;
        while (!currentPoint.equals(startPoint)) {
            for (int i = 0; i < 8; i++) {
                if (isValidCoordinate(nextPoint(currentPoint, i))
                        && field.getPointValue(nextPoint(currentPoint, i)) == currentStep - 1) {
                    currentPoint = nextPoint(currentPoint, i);
                    currentStep--;
                    pathList.add(Converter.convertPointToChessCoordinate(currentPoint));
                    continue;
                }
            }
        }
        Collections.reverse(pathList);
        StringBuilder sbPath = new StringBuilder();
        for (String s : pathList) {
            sbPath.append(s).append(" ");
        }
        return sbPath.toString().trim();
    }

    /**
     * Проверка координаты на валидность
     * 
     * @param arg
     * @return true если координата в аргументе валидна
     */
    static boolean isValidArg(final String arg) {
        String regex = "[a-hA-H][1-8]";
        if (!arg.matches(regex)) {
            return false;
        }
        return true;
    }

    /**
     * Генерация следующей точки по правилу хождения шахматного коня
     * 
     * @param point начальная точка
     * @param moveOption вариант хода [0-7]
     * @return точка следующего хода
     */
    static Point nextPoint(final Point point, final int moveOption) {
        int[][] possibleMoves =
                {{1, 2}, {2, 1}, {-1, -2}, {2, -1}, {1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
        int moveX = possibleMoves[moveOption][0];
        int moveY = possibleMoves[moveOption][1];
        int x = (int) point.getX();
        int y = (int) point.getY();
        return new Point(x + moveX, y + moveY);
    }

    /**
     * Проставляет всем возможным ходам коня из заданной точки значение step+1
     * 
     * @param point начальная точка
     * @param step номер хода
     * @param field экземляр Field
     */
    static void incrementStep(Point point, int step, Field field) {
        for (int k = 0; k < 8; k++) {
            Point nextPoint = nextPoint(point, k);
            if (isValidCoordinate(nextPoint) && !Field.isOccupied(nextPoint)) {
                int x = (int) nextPoint.getX();
                int y = (int) nextPoint.getY();
                field.setPointsWithValues(x, y, step + 1);
                field.setOccupiedPoints(nextPoint);
            }
        }
    }

    /**
     * Вычисляет минимальное количество ходов коня из точки в точку
     * 
     * @param startPoint
     * @param finishPoint
     * @param field экземляр Field
     * @return количество ходов из startPoint в finishPoint
     */
    static int countMoves(final Point startPoint, final Point finishPoint, Field field) {

        if (startPoint.equals(finishPoint)) {
            return 0;
        }

        field.setOccupiedPoints(startPoint);
        int step = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field.setPointsWithValues(i, j, step);
            }
        }
        incrementStep(startPoint, step, field);
        while (field.getPointValue(finishPoint) == 0) {
            step++;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (field.getPointsWithValues()[i][j] == step) {
                        Point pointNow = new Point(i, j);
                        incrementStep(pointNow, step, field);
                    }
                }
            }
        }
        return field.getPointValue(finishPoint); // возвращает значение step на финальной точке
    }

    /**
     * Проверяет точку на нахождение в поле
     * 
     * @param point точка на проверку
     * @return true если точка валидна
     */
    static boolean isValidCoordinate(final Point point) {
        return point.getX() >= 0 && point.getX() < 8 && point.getY() >= 0 && point.getY() < 8;
    }

}
