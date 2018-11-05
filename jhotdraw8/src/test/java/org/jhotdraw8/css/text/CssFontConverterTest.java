package org.jhotdraw8.css.text;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CssFontConverterTest {
    /**
     * Test of fromString method, of class CssFontConverter.
     */
    static
    public void doTestFromString(CssFont expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssFontConverter instance = new CssFontConverter(false);
        CssFont actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(
                        new CssFont("Arial", FontWeight.NORMAL, FontPosture.REGULAR,new CssSize(12)),
                        "12 Arial")),
                dynamicTest("2", () -> doTestFromString(
                        new CssFont("Arial", FontWeight.NORMAL, FontPosture.REGULAR,new CssSize(12,"pt")),
                        "12pt Arial")),
                dynamicTest("3", () -> doTestFromString(
                        new CssFont("Arial", FontWeight.SEMI_BOLD, FontPosture.REGULAR,new CssSize(12,"pt")),
                        "600 12pt Arial"))
        );
    }

}