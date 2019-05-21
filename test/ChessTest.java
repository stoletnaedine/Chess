import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

public class ChessTest {

    @Test
    public void testIsValidArgsWithValidPoints() {
        String[] validArgs = {"a1", "h8"};
        assertTrue(Chess.isValidArgs(validArgs));
    }

    @Test
    public void testIsValidArgsWithInvalidPoints() {
        String[] invalidArgs = {"k1", "x8"};
        assertFalse(Chess.isValidArgs(invalidArgs));
    }

    @Test
    public void testIsValidArgsWithInvalidLength() {
        String[] invalidArgs = {"a01", "b08"};
        assertFalse(Chess.isValidArgs(invalidArgs));
    }

    @Test
    public void testIsValidArgsWithOneArg() {
        String[] invalidArgs = {"a1"};
        assertFalse(Chess.isValidArgs(invalidArgs));
    }

    @Test
    public void testIsValidArgsWithNoArgs() {
        String[] nullArgs = {null};
        assertFalse(Chess.isValidArgs(nullArgs));
    }

    @Test
    public void testNextPoint() {
        Point currentPoint = new Point(1, 1);
        Point expectedPoint = new Point(2, 3);
        assertEquals(expectedPoint, Chess.nextPoint(currentPoint, 0));
    }

    @Test
    public void testIncrementStep() {
        Point startPoint = new Point(0, 0);
        Point nextPoint = new Point(2, 1);
        int startStepValue = 0;
        int expectedStepValue = 1;
        Chess.pointsWithValues[0][0] = startStepValue;
        Chess.incrementStep(startPoint, 0);
        assertEquals(expectedStepValue, Chess.getPointValue(nextPoint));
    }

    @Test
    public void testCountMovesFromA1ToA1() {
        Chess chess = new Chess();
        Point startPoint = new Point(0, 0);
        Point endPoint = new Point(0, 0);
        int expectedCounts = 0;
        assertEquals(expectedCounts, chess.countMoves(startPoint, endPoint));
    }

    @Test
    public void testCountMovesFromA1ToH8() {
        Chess chess = new Chess();
        Point startPoint = new Point(0, 0);
        Point endPoint = new Point(7, 7);
        int expectedMoves = 6;
        assertEquals(expectedMoves, chess.countMoves(startPoint, endPoint));
    }

    @Test
    public void testGetPointValue() {
        Point point = new Point(0, 0);
        int currentValue = 5;
        Chess.pointsWithValues[(int) point.getX()][(int) point.getY()] = currentValue;
        assertEquals(currentValue, Chess.getPointValue(point));
    }

    @Test
    public void testIsOccupiedIfTrue() {
        Point point = new Point(5, 5);
        Chess.occupiedPoints.add(point);
        assertTrue(Chess.isOccupied(point));
    }

    @Test
    public void testIsOccupiedIfFalse() {
        Point point = new Point(5, 5);
        assertTrue(Chess.isOccupied(point));
    }

    @Test
    public void testIsValidCoordinateIfTrue() {
        Point validPoint = new Point(5, 5);
        assertTrue(Chess.isValidCoordinate(validPoint));
    }

    @Test
    public void testIsValidCoordinateIfFalse() {
        Point invalidPoint = new Point(15, 15);
        assertFalse(Chess.isValidCoordinate(invalidPoint));
    }

    @Test
    public void testConvertArgToPointA1() {
        String coordinate = "a1";
        Point expectedPoint = new Point(0, 0);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointB1() {
        String coordinate = "b1";
        Point expectedPoint = new Point(1, 0);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointC2() {
        String coordinate = "c2";
        Point expectedPoint = new Point(2, 1);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointD3() {
        String coordinate = "d3";
        Point expectedPoint = new Point(3, 2);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointE2() {
        String coordinate = "e2";
        Point expectedPoint = new Point(4, 1);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointF5() {
        String coordinate = "f5";
        Point expectedPoint = new Point(5, 4);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointG7() {
        String coordinate = "g7";
        Point expectedPoint = new Point(6, 6);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointH3() {
        String coordinate = "h3";
        Point expectedPoint = new Point(7, 2);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

    @Test
    public void testConvertArgToPointX0() {
        String coordinate = "z3";
        Point expectedPoint = new Point(-1, 2);
        assertEquals(expectedPoint, Chess.convertArgToPoint(coordinate));
    }

}
