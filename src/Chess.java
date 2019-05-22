import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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

    private static int[][] pointsWithValues = new int[WIDTH][HEIGHT]; // инициализируем шахматное
                                                                      // поле

    public static int[][] getPointsWithValues() {
        return pointsWithValues;
    }

    public static void setPointsWithValues(final int x, final int y, final int value) {
        pointsWithValues[x][y] = value;
    }

    public static ArrayList<Point> getOccupiedPoints() {
        return occupiedPoints;
    }

    public static void setOccupiedPoints(final Point point) {
        Chess.occupiedPoints.add(point);
    }

    private static ArrayList<Point> occupiedPoints = new ArrayList<>(); // список занятых клеток

    private static String pathXML;

    private static boolean isTask = false;

    private static boolean isShowPath = false;

    private static Point startPoint;

    private static Point finishPoint;

    private static String coordinateOne;

    private static String coordinateTwo;

    public static void main(String[] args)
            throws SAXException, ParserConfigurationException, DocumentException {
        StatusLogger.getLogger().setLevel(Level.OFF);
        int counts; // минимальное число ходов коня

        if (args.length == 2 || args.length == 3) {
            parseArgs(args);
            if ((startPoint != null && finishPoint != null) || isTask) {
                if (isTask) {
                    parseXML(pathXML);
                } else {
                    coordinateOne = convertPointToChessCoordinate(startPoint);
                    coordinateTwo = convertPointToChessCoordinate(finishPoint);
                }
                counts = countMoves(startPoint, finishPoint);
                LOG.info("Из клетки {} в клетку {} трeбуется ходов коня: {}.", coordinateOne,
                        coordinateTwo, counts);
                if (isShowPath) {
                    LOG.info("Кратчайший путь: {}", printShowPath(startPoint, finishPoint, counts));
                }
            } else {
                LOG.error("Неверные аргументы.\nКоординаты [a-h][1-8]\n"
                        + "Аргументы в XML: --task {name}.xml\nПоказать путь(опционально): --show-path");
            }
        } else {
            LOG.error("Неверное количество аргументов.");
        }
    }

    static void parseArgs(String[] args) {
        if (args.length == 3) {
            for (int pos = 0; pos < args.length; pos++) {
                if (args[pos].equals("--show-path")) {
                    isShowPath = true;
                }
                if (args[pos].equals("--task")) {
                    isTask = true;
                    pathXML = args[pos + 1];
                } else if (args[pos].equals("--show-path") && pos == 0) {
                    if (isValidArg(args[pos + 1]) && isValidArg(args[pos + 2])) {
                        startPoint = convertArgToPoint(args[pos + 1]);
                        finishPoint = convertArgToPoint(args[pos + 2]);
                    }

                } else if (args[pos].equals("--show-path") && pos == 2) {
                    if (isValidArg(args[pos - 2]) && isValidArg(args[pos - 1])) {
                        startPoint = convertArgToPoint(args[pos - 2]);
                        finishPoint = convertArgToPoint(args[pos - 1]);
                    }
                }
            }
        }
        if (args.length == 2) {
            for (int pos = 0; pos < args.length; pos++) {
                if (args[pos].equals("--task")) {
                    isTask = true;
                    pathXML = args[Math.abs(pos - 1)];
                } else if (isValidArg(args[0]) && isValidArg(args[1])) {
                    startPoint = convertArgToPoint(args[0]);
                    finishPoint = convertArgToPoint(args[1]);
                }
            }
        }
    }

    static String printShowPath(Point startPoint, Point finishPoint, int counts) {
        ArrayList<String> pathList = new ArrayList<>();
        Point currentPoint = finishPoint;
        pathList.add(convertPointToChessCoordinate(finishPoint));
        int currentStep = counts;
        while (!currentPoint.equals(startPoint)) {
            for (int i = 0; i < 8; i++) {
                if (isValidCoordinate(nextPoint(currentPoint, i))
                        && getPointValue(nextPoint(currentPoint, i)) == currentStep - 1) {
                    currentPoint = nextPoint(currentPoint, i);
                    currentStep--;
                    pathList.add(convertPointToChessCoordinate(currentPoint));
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

    static boolean isValidArg(String arg) { // проверка аргумента
        String regex = "[a-hA-H][1-8]";
        if (!arg.matches(regex)) {
            return false;
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

    static int countMoves(Point startPoint, Point finishPoint) {

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
        while (getPointValue(finishPoint) == 0) {
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

    static Point convertArgToPoint(String coordinate) { // конвертация шахматной
                                                        // координаты в Point
        int x = -1;
        int y = -1;
        if (coordinate.length() != 2) {
            LOG.error("Неверно заданы координаты");
        } else {
            char letter = coordinate.charAt(0);
            letter = Character.toLowerCase(letter);
            y = Character.getNumericValue(coordinate.charAt(1) - 1);
            switch (letter) {
            case 'a':
                x = 0;
                break;
            case 'b':
                x = 1;
                break;
            case 'c':
                x = 2;
                break;
            case 'd':
                x = 3;
                break;
            case 'e':
                x = 4;
                break;
            case 'f':
                x = 5;
                break;
            case 'g':
                x = 6;
                break;
            case 'h':
                x = 7;
                break;
            default:
                break;
            }
        }
        return new Point(x, y);
    }

    static String convertPointToChessCoordinate(Point point) { // конвертация Point в шахматную
                                                               // координату
        char letter = 0;
        StringBuilder sb = new StringBuilder();
        switch (point.x) {
        case 0:
            letter = 'a';
            break;
        case 1:
            letter = 'b';
            break;
        case 2:
            letter = 'c';
            break;
        case 3:
            letter = 'd';
            break;
        case 4:
            letter = 'e';
            break;
        case 5:
            letter = 'f';
            break;
        case 6:
            letter = 'g';
            break;
        case 7:
            letter = 'h';
            break;
        default:
            break;
        }
        sb.append(letter);
        sb.append(point.y + 1);
        return sb.toString();
    }

    static void parseXML(final String pathXML)
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
            public void warning(final SAXParseException exception) throws SAXException {
                LOG.info("Warning: " + exception);

            }

            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
                LOG.fatal("Fatal Error: " + exception);

            }

            @Override
            public void error(final SAXParseException exception) throws SAXException {
                LOG.error("Error: " + exception);

            }
        };
        reader.setErrorHandler(errorHandler);
        File XMLFile = new File(pathXML);
        Document document = reader.read(XMLFile);

        Element root = document.getRootElement();

        for (Element element : root.elements()) {
            if (element.getName().equals("start")) {
                coordinateOne = element.getText();
                startPoint = convertArgToPoint(coordinateOne);
            }
            if (element.getName().equals("finish")) {
                coordinateTwo = element.getText();
                finishPoint = convertArgToPoint(coordinateTwo);
            }
        }

    }

}
