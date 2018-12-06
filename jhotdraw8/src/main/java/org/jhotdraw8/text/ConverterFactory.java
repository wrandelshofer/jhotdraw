/* @(#)ConverterFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.util.function.BiFunction;
import javax.annotation.Nullable;

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
    public Converter<?> apply(@Nullable String type, @Nullable String style);
}
