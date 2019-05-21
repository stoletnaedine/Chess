import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Chess {

    private static final Logger LOG = LogManager.getLogger(Chess.class);

    private static final int WIDTH = 8;

    private static final int HEIGHT = 8;

    static int[][] pointsWithValues = new int[WIDTH][HEIGHT]; // инициализируем шахматное поле

    static ArrayList<Point> occupiedPoints = new ArrayList<>(); // список занятых клеток

    static ArrayList<Point> holes = new ArrayList<>(); // список "дырок"

    static String pathXML;

    static Point startPoint;

    static Point finishPoint;

    static String pointOne;

    static String pointTwo;

    public static void main(String[] args)
            throws SAXException, ParserConfigurationException, DocumentException {
        StatusLogger.getLogger().setLevel(Level.OFF);
        int counts; // минимальное число ходов коня
        Chess chess = new Chess();

        if (isValidArgs(args)) {
            if (args[0].equals("--task")) {
                pathXML = args[1];
                chess.parseXML(pathXML);
            } else {
                pointOne = args[0];
                pointTwo = args[1];
                startPoint = convertArgToPoint(args[0]);
                finishPoint = convertArgToPoint(args[1]);
            }
            if (startPoint.equals(finishPoint)) {
                counts = 0;
            } else {
                counts = chess.countMoves(startPoint, finishPoint);
            }
            LOG.info("Из клетки {} в клетку {} трeбуется ходов коня: {}.", pointOne, pointTwo,
                    counts);
        }
    }

    void parseXML(String XMLpath)
            throws SAXException, ParserConfigurationException, DocumentException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setSchema(schemaFactory.newSchema(
                new Source[] {new StreamSource(Chess.class.getResourceAsStream("task.xsd"))}));
        SAXParser parser = factory.newSAXParser();
        SAXReader reader = new SAXReader(parser.getXMLReader());
        reader.setValidation(false);
        ErrorHandler errorHandler = new ErrorHandler() {

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                LOG.info("Warning: " + exception);

            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                LOG.fatal("Fatal Error: " + exception);

            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                LOG.error("Error: " + exception);

            }
        };
        reader.setErrorHandler(errorHandler);
        File XMLFile = new File(XMLpath);
        Document document = reader.read(XMLFile);

        Element root = document.getRootElement();

        for (Element element : root.elements()) {
            if (element.getName().equals("start")) {
                pointOne = element.getText();
                startPoint = convertArgToPoint(pointOne);
            }
            if (element.getName().equals("finish")) {
                pointTwo = element.getText();
                finishPoint = convertArgToPoint(pointTwo);
            }
        }

    }

    static boolean isValidArgs(String[] args) { // проверка аргументов на валидность
        if (args.length != 2) {
            LOG.error(
                    "Введено неверное количество параметров ('a1 c8' или '--task from-a1-to-b1.xml')");
            return false;
        } else {
            if (args[0].equals("--task")) {
                return true;
            }
            if (args[0].length() != 2 || args[1].length() != 2
                    || !isValidCoordinate(convertArgToPoint(args[0]))
                    || !isValidCoordinate(convertArgToPoint(args[1]))) {
                LOG.error("Неверные аргументы ('a1 c8' или '--task from-a1-to-b1.xml')");
                return false;
            }
        }
        return true;
    }

    static Point nextPoint(Point point, int moveOption) { // отдает координату следующего хода
        int[][] possibleMoves =
                {{1, 2}, {2, 1}, {-1, -2}, {2, -1}, {1, -2}, {-2, -1}, {-2, 1}, {-1, 2}}; // возможные
                                                                                          // варианты
                                                                                          // ходов
                                                                                          // коня
        int moveX = possibleMoves[moveOption][0];
        int moveY = possibleMoves[moveOption][1];
        int x = (int) point.getX();
        int y = (int) point.getY();
        return new Point(x + moveX, y + moveY);
    }

    static void incrementStep(Point point, int step) { // ставим всем возможным ходам шаг step+1
        for (int k = 0; k < 8; k++) { // перебираем варианты хода коня
            Point nextPoint = nextPoint(point, k);
            if (isValidCoordinate(nextPoint) && !isOccupied(nextPoint)) { // если ход возможен и
                                                                          // клетка свободна
                int x = (int) nextPoint.getX();
                int y = (int) nextPoint.getY();
                pointsWithValues[x][y] = step + 1;
                occupiedPoints.add(nextPoint); // добавляем клетку в список занятых
            }
        }
    }

    int countMoves(Point startPoint, Point finishPoint) {

        if (startPoint.equals(finishPoint)) {
            return 0;
        }

        occupiedPoints.add(startPoint);
        int step = 0; // начальное количество шагов у стартовой клетки
        for (int i = 0; i < 8; i++) { // заполняем все поле step == 0
            for (int j = 0; j < 8; j++) {
                pointsWithValues[i][j] = step;
            }
        }
        incrementStep(startPoint, step); // ставим step+1 всем возможным ходам с начальной клетки
        while (pointsWithValues[(int) finishPoint.getX()][(int) finishPoint.getY()] == 0) {
            step++;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (pointsWithValues[i][j] == step) { // у всех клеток == step ищем ходы и
                                                          // присваиваем step+1
                        Point pointNow = new Point(i, j);
                        incrementStep(pointNow, step);
                    }
                }
            }
        }
        return getPointValue(finishPoint); // возвращает значение step на финальной точке
    }

    static int getPointValue(Point point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
        return pointsWithValues[x][y];
    }

    static boolean isOccupied(Point point) {
        for (Point p : occupiedPoints) {
            if (p.equals(point)) {
                return true;
            }
        }
        return false;
    }

    static boolean isValidCoordinate(Point point) { // проверка координаты на валидность
        return point.getX() >= 0 && point.getX() < 8 && point.getY() >= 0 && point.getY() < 8;
    }

    static Point convertArgToPoint(String coordinate) { // конвертация аргумента в Point
        char letter = coordinate.charAt(0);
        letter = Character.toLowerCase(letter);
        int X = -1;
        int Y = Character.getNumericValue(coordinate.charAt(1) - 1);
        switch (letter) {
        case 'a':
            X = 0;
            break;
        case 'b':
            X = 1;
            break;
        case 'c':
            X = 2;
            break;
        case 'd':
            X = 3;
            break;
        case 'e':
            X = 4;
            break;
        case 'f':
            X = 5;
            break;
        case 'g':
            X = 6;
            break;
        case 'h':
            X = 7;
            break;
        default:
            X = -1;
            break;
        }
        return new Point(X, Y);
    }

}
