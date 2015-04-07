/* @(#)FigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;

/**
 * FigureFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureFactory {

    /** Maps a figure to a name.
    */
    String figureToName(Figure f) throws IOException;

    /** Maps a name to a figure. */
    Figure nameToFigure(String name) throws IOException;

    /** Maps a key to a name. 
     * The name used for persistent storage may be different from the name
     * defined in the key.
     */
    String keyToName(Key<?> key) throws IOException;

    /** Maps a name to a key. */
    Key<?> nameToKey(String name) throws IOException;

    /** Maps a value to a String. */
    String valueToString(Key<?> key, Object value) throws IOException;

    /** Maps a String to a value. */
    Object stringToValue(Key<?> key, String cdata) throws IOException;

    /** Returns the default for the key.
     * The default value used for persistent storage may be different from
     * the default value defined in the key.
     */
    <T> T getDefaultValue(Key<T> key);

    /** Returns true if the specified value is the default for the given key.*/
    default <T> boolean isDefaultValue(Key<T> key, T value) {
        T defaultValue = key.getDefaultValue();
        return defaultValue == null ? value == null : (value == null ? false : defaultValue.equals(value));
    }

    /** Returns all persistent keys for the specified figure.
     * @param f The figure
     * @return an immutable set
    */
    Set<Key<?>> figureKeys(Figure f);
}
