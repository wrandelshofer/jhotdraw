/* @(#)DoubleListStyleConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import com.sun.javafx.css.Size;
import java.util.ArrayList;
import java.util.List;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

/**
 * DoubleListStyleConverter.
 *
 * @author Werner Randelshofer
 */
public class DoubleListStyleConverter extends StyleConverter<ParsedValue[], List<Double>> {

    private static DoubleListStyleConverter instance;

    private DoubleListStyleConverter() {
    }

    public static DoubleListStyleConverter getInstance() {
        if (instance == null) {
            instance = new DoubleListStyleConverter();
        }
        return instance;
    }

    @Override
    public List<Double> convert(ParsedValue<ParsedValue[], List<Double>> value, Font font) {
        ParsedValue[] sides = value.getValue();
        ArrayList<Double> list = new ArrayList<>(sides.length);
        for (int i = 0; i < sides.length; i++) {
            double item = ((Size) sides[i].convert(font)).pixels(font);
            list.add(item);
        }
        return list;
    }

}
