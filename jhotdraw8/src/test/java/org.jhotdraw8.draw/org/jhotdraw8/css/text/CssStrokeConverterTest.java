package org.jhotdraw8.css.text;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStroke;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssStrokeConverterTest.
 *
 * @author Werner Randelshofer
 */
class CssStrokeConverterTest {

    private static final String IDENT_NONE = CssTokenType.IDENT_NONE;

    /**
     * Test of fromString method, of class CssStrokeStyleConverter.
     */
    static
    public void doTestFromString(CssStroke expected, String string) throws Exception {
        System.out.println("fromString " + string);
        if (string.equals(IDENT_NONE)) {
            expected = null;
        }

        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssStrokeStyleConverter instance = new CssStrokeStyleConverter(false);
        CssStroke actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of toString method, of class CssStrokeStyleConverter.
     */
    static
    public void doTestToString(CssStroke value, String expected) throws Exception {
        System.out.println("toString " + value);
        CssStrokeStyleConverter instance = new CssStrokeStyleConverter(false);
        String actual = instance.toString(value);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of fromString and toString methods, of class CssStrokeStyleConverter.
     */
    static
    public void doTest(CssStroke value, String str) throws Exception {
        doTestFromString(value, str);
        doTestToString(value, str);
    }


    @TestFactory
    public List<DynamicTest> testFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTest(
                        new CssStroke(CssColor.BLACK),
                        "1 black butt miter miter-limit(4) dash-offset(0) dash-array()")),
                dynamicTest("2", () -> doTest(
                        new CssStroke(new CssSize(2), CssColor.BLACK),
                        "2 black butt miter miter-limit(4) dash-offset(0) dash-array()")),
                dynamicTest("3", () -> doTest(
                        new CssStroke(new CssSize(2), CssColor.BLACK, StrokeType.CENTERED, StrokeLineCap.ROUND, StrokeLineJoin.MITER, new CssSize(3)
                                , new CssSize(4), ImmutableLists.of(new CssSize(5), new CssSize(6))),
                        "2 black round miter miter-limit(3) dash-offset(4) dash-array(5 6)")),
                dynamicTest("4", () -> doTest(
                        new CssStroke(new CssSize(2), CssColor.BLACK, StrokeType.CENTERED, StrokeLineCap.BUTT, StrokeLineJoin.MITER, new CssSize(3)
                                , new CssSize(4), ImmutableLists.of(new CssSize(5), new CssSize(6))),
                        "2 black butt miter miter-limit(3) dash-offset(4) dash-array(5 6)")),
                dynamicTest("5", () -> doTest(
                        new CssStroke(new CssSize(2), CssColor.BLACK, StrokeType.INSIDE, StrokeLineCap.ROUND, StrokeLineJoin.MITER, new CssSize(3)
                                , new CssSize(4), ImmutableLists.of(new CssSize(5), new CssSize(6))),
                        "2 black inside round miter miter-limit(3) dash-offset(4) dash-array(5 6)")),
                dynamicTest("6", () -> doTest(
                        new CssStroke(new CssSize(1), null, StrokeType.CENTERED, StrokeLineCap.SQUARE, StrokeLineJoin.MITER, new CssSize(10)
                                , new CssSize(0), ImmutableLists.emptyList()),
                        IDENT_NONE))
        );
    }

}