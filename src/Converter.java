import java.awt.Point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Converter {

    private static final Logger LOG = LogManager.getLogger(Converter.class);

    /**
     * Конвертирует аргумент шахматную координату String в точку Point
     * 
     * @param coordinate
     * @return точку Point
     */
    public static Point convertArgToPoint(final String coordinate) {
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

    /**
     * Rонвертирует точку Point в шахматную координату String
     * 
     * @param point
     * @return конвертирует Point в String
     */
    public static String convertPointToChessCoordinate(final Point point) {
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

}
