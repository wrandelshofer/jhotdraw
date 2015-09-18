/* @(#)ConverterFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.text.Format;
import java.util.function.BiFunction;

/**
 * Creates a {@code Converter} given a type and style.
 * <p>
 * The factory is allowed to return an already existing converter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ConverterFactory extends BiFunction<String, String, Converter<?>> {

    /**
     * Returns a {@code Converter} given a type and a style.
     *
     * @param type the type, may be null
     * @param style the style, may be null
     * @return the converter
     *
     * @throws IllegalArgumentException if the type or the style are invalid
     */
    @Override
    public Converter<?> apply(String type, String style);
}
