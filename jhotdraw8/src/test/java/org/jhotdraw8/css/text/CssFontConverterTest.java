package org.jhotdraw8.css.text;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssFont;
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
        assertEquals(expected, actual);
    }
    /**
     * Test of toString method, of class CssFontConverter.
     */
    static
    public void doTestToString(CssFont value, String expected) throws Exception {
        System.out.println("toString " + value);
        CssFontConverter instance = new CssFontConverter(false);
        String actual = instance.toString(value);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected,actual);
    }
    /**
     * Test of fromString and toString methods, of class CssFontConverter.
     */
    static
    public void doTest(CssFont value, String str) throws Exception {
        doTestFromString(value,str);
        doTestToString(value,str);
    }

    @TestFactory
    public List<DynamicTest> testFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTest(
                        new CssFont("Arial", FontWeight.NORMAL, FontPosture.REGULAR,new CssSize(12)),
                        "12 Arial")),
                dynamicTest("2", () -> doTest(
                        new CssFont("Arial", FontWeight.NORMAL, FontPosture.REGULAR,new CssSize(12,"pt")),
                        "12pt Arial")),
                dynamicTest("3", () -> doTest(
                        new CssFont("Arial", FontWeight.SEMI_BOLD, FontPosture.REGULAR,new CssSize(12,"pt")),
                        "600 12pt Arial"))
        );
    }

}