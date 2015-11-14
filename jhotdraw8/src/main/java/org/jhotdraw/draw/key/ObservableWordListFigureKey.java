/* @(#)ObservableWordListStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.List;
import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.styleable.StyleableKey;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssObservableWordListConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * ObservableWordListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class ObservableWordListFigureKey extends SimpleFigureKey<ObservableList<String>> {

    private final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public ObservableWordListFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public ObservableWordListFigureKey(String name, ObservableList<String> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public ObservableWordListFigureKey(String name, DirtyMask mask, ObservableList<String> defaultValue) {
        super(name, List.class, "<Double>", mask, defaultValue);
    }
}
