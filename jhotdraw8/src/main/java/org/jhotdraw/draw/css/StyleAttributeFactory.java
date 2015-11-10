/* @(#)StyleAttributeFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.io.IOException;
import java.util.List;
import org.jhotdraw.collection.Key;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.css.ast.Term;
import org.jhotdraw.draw.Figure;

/**
 * StyleAttributeFactory creates {@code Key}s and values from CSS property
 * names and CSS terms.
 * 
 * @author Werner Randelshofer
 */
public interface StyleAttributeFactory {
   
    /**
     * Maps a CSS property name to a key.
     *
     * @param f the figure
     * @param propertyName the name
     * @return the key
     *
     * @throws java.io.IOException if the factory does not support the name for
     * the specified figure
     */
    Key<?> cssDeclarationToValue(Figure f, String propertyName) throws IOException;
    
    /**
     * Maps a CSS declaration to a value.
     *
     * @param key the key
     * @param terms the CSS declaration
     * @return the mapped value
     *
     * @throws java.io.IOException if the factory does not support a mapping for
     * the specified key
     */
    Object cssDeclarationToValue(Key<?> key, Declaration declaration) throws IOException;
}
