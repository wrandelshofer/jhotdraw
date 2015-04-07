/* @(#)Converter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.text.ParseException;
import java.text.ParsePosition;
import javafx.util.converter.DoubleStringConverter;

/**
 * Converts a value to a String and vice versa.
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> The value type
 */
public interface Converter<T> {

    /** Converts a value to a String. 
     *
     * @param value The value
     * @return The String
     */
    String toString(T value);

    /** Converts a String to a value.
     *
     * @param string The String
     * @return The value
     * @throws ParseException if conversion fails.
     */
    default T toValue(String string) throws ParseException {
        ParsePosition pp = new ParsePosition(0);
        T value = toValue(string, pp);
        if (pp.getErrorIndex() != -1) {
            throw new ParseException("unexpected chars", pp.getErrorIndex());
        }
        if (pp.getIndex() != string.length()) {
            throw new ParseException("unexpected chars", pp.getIndex());
        }
        return value;
    }

    /** Converts a substring to a value.
     *
     * @param string The String
     * @return The value
     * @param pp The parse position giving the start position.
     *           Updates the {@code index} on success.
     *            Sets the {@code errorIndex} on failure.
     */
    T toValue(String string, ParsePosition pp);
}
