package org.jhotdraw8.css.text;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStrokeStyle;
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
    public void doTestFromString(CssStrokeStyle expected, @NonNull String string) throws Exception {
        System.out.println("fromString " + string);

        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssStrokeStyleConverter instance = new CssStrokeStyleConverter(false);
        CssStrokeStyle actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of toString method, of class CssStrokeStyleConverter.
     */
    static
    public void doTestToString(CssStrokeStyle value, String expected) throws Exception {
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
    public void doTest(CssStrokeStyle value, @NonNull String str) throws Exception {
        doTestFromString(value, str);
        doTestToString(value, str);
    }


    @NonNull
    @TestFactory
    public List<DynamicTest> testFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTest(
                        new CssStrokeStyle(),
                        "type(centered) linecap(butt) linejoin(miter) miterlimit(4) dashoffset(0) dasharray()")),
                dynamicTest("2", () -> doTest(
                        new CssStrokeStyle(),
                        "type(centered) linecap(butt) linejoin(miter) miterlimit(4) dashoffset(0) dasharray()")),
                dynamicTest("3", () -> doTest(
                        new CssStrokeStyle(StrokeType.CENTERED, StrokeLineCap.ROUND, StrokeLineJoin.MITER, new CssSize(3)
                                , new CssSize(4), ImmutableLists.of(new CssSize(5), new CssSize(6))),
                        "type(centered) linecap(round) linejoin(miter) miterlimit(3) dashoffset(4) dasharray(5 6)")),
                dynamicTest("4", () -> doTest(
                        new CssStrokeStyle(StrokeType.CENTERED, StrokeLineCap.BUTT, StrokeLineJoin.MITER, new CssSize(3)
                                , new CssSize(4), ImmutableLists.of(new CssSize(5), new CssSize(6))),
                        "type(centered) linecap(butt) linejoin(miter) miterlimit(3) dashoffset(4) dasharray(5 6)")),
                dynamicTest("5", () -> doTest(
                        new CssStrokeStyle(StrokeType.INSIDE, StrokeLineCap.ROUND, StrokeLineJoin.MITER, new CssSize(3)
                                , new CssSize(4), ImmutableLists.of(new CssSize(5), new CssSize(6))),
                        "type(inside) linecap(round) linejoin(miter) miterlimit(3) dashoffset(4) dasharray(5 6)")),
                dynamicTest("6", () -> doTest(
                        new CssStrokeStyle(StrokeType.CENTERED, StrokeLineCap.BUTT, StrokeLineJoin.MITER, new CssSize(4)
                                , new CssSize(0), ImmutableLists.emptyList()),
                        "type(centered) linecap(butt) linejoin(miter) miterlimit(4) dashoffset(0) dasharray()"))
        );
    }

}