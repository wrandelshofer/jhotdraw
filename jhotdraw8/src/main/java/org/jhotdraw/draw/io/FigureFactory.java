/* @(#)FigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;

/**
 * FigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureFactory {

    /** Maps a figure to an XML element name.
     *
     * @param f the figure
     * @return the name
     *
     * @throws java.io.IOException if the factory does not support this figure
     */
    String figureToName(Figure f) throws IOException;

    /** Maps an XML element name to a figure.
     *
     * @param name the name
     * @return the figure
     *
     * @throws java.io.IOException if the factory does not support this name
     */
    Figure nameToFigure(String name) throws IOException;

    /** Maps a key to a XML attribute name.
     * The name used for persistent storage may be different from the name
     * defined in the key.
     *
     * @param f the figure
     * @param key the key
     * @return the name
     *
     * @throws java.io.IOException if the factory does not support the key for
     * the specified figure
     */
    String keyToName(Figure f, Key<?> key) throws IOException;

    /** Maps an XML attribute name to a key.
     *
     * @param f the figure
     * @param name the name
     * @return the key
     *
     * @throws java.io.IOException if the factory does not support the name
     * for the specified figure
     */
    Key<?> nameToKey(Figure f, String name) throws IOException;

    /** Maps a value to an XML attribute value.
     *
     * @param key the key
     * @param value the value
     * @return the mapped attribute value
     *
     * @throws java.io.IOException if the factory does not support a mapping
     * for the specified key
     */
    String valueToString(Key<?> key, Object value) throws IOException;

    /** Maps an XML attribute value to a value.
     *
     * @param key the key
     * @param cdata the XML attribute value
     * @return the mapped value
     *
     * @throws java.io.IOException if the factory does not support a mapping
     * for the specified key
     */
    Object stringToValue(Key<?> key, String cdata) throws IOException;

    /** Returns the default for the key.
     * The default value used for persistent storage may be different from
     * the default value defined in the key.
     *
     * @param <T> The type of the value
     * @param key The key
     * @return the default value
     */
    <T> T getDefaultValue(Key<T> key);

    /** Returns true if the specified value is the default for the given key.
     *
     * @param <T> The type of the value
     * @param key The key
     * @param value the value
     * @return true if the value is the default value
     */
    default <T> boolean isDefaultValue(Key<T> key, T value) {
        T defaultValue = key.getDefaultValue();
        return defaultValue == null ? value == null : (value == null ? false : defaultValue.equals(value));
    }

    /** Returns all persistent keys for the specified figure.
     *
     * @param f The figure
     * @return an immutable set
     */
    Set<Key<?>> figureKeys(Figure f);
}
