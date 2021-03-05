/*
 * @(#)SvgColorTest.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.text.CssColorConverter;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CssColorConverterTest {
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsSvgColor() throws IOException {
        return List.of(
                DynamicTest.dynamicTest("reddish", () -> testSvgColor(
                        "#cfffff", Color.web("#cfffff"))),
                DynamicTest.dynamicTest("greenish", () -> testSvgColor(
                        "rgb(20%,73.333%,20%)", Color.web("#33bb33")))
        )
                ;

    }

    private void testSvgColor(String inputStr, Color expected) throws IOException, ParseException {
        CssColorConverter c = new CssColorConverter();
        CssColor actual = c.fromString(inputStr);
        String actualStr = c.toString(actual);
        CssColor cssColorRecreatedFromJavaFXColor = new CssColor(actual.getColor());
        String recreatedActualStr = cssColorRecreatedFromJavaFXColor.getName();
        CssColor recreatedColor = c.fromString(recreatedActualStr);
        Color recreated = recreatedColor.getColor();
        System.out.println("inputStr     " + inputStr);
        System.out.println("actualStr    " + actualStr);
        System.out.println("recreatedStr " + recreatedActualStr);
        System.out.println("expected     " + expected.getRed() + "," + expected.getGreen() + "," + expected.getBlue() + "," + expected.getOpacity());
        System.out.println("recreated    " + recreated.getRed() + "," + recreated.getGreen() + "," + recreated.getBlue() + "," + recreated.getOpacity());

        assertEquals(expected.toString(), actual.getColor().toString());
        assertEquals(inputStr, actualStr);
        assertEquals(expected.toString(), recreatedColor.getColor().toString());
    }
}
