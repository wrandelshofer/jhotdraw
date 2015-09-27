/*
 * @(#)Converter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts a data value of type {@code T} from or to a String representation.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> The type of the data value
 */
public interface Converter<T> {

    /** Converts a value to a string and appends it to the provided
     * {@code Appendable}.
     * <p>
     * This method does not change the state of the converter. 
     *
     * @param value The value. Nullable.
     * @param out The appendable
     * @throws java.io.IOException thrown by Appendable
     */
    void toString(Appendable out, T value) throws IOException;

    /**
     * Converts a value to a String.
     * <p>
     * This method does not change the state of the converter. 
     * <p>
     * Note: this is a convenience method. Implementing classes rarely need
     * to overwrite this method.
     *
     * @param value The value. Nullable.
     * @return The String.
     */
    default String toString(T value) {
        StringBuilder out = new StringBuilder();
        try {
            toString(out,value);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        return out.toString();
    }

    /**
     * Constructs a value from a string.
     * <p>
     * The converter should try to create the value greedily, by consuming
     * as many characters as possible for the value.
     * <p>
     * This method does not change the state of the converter. 
     *
     * @param in A char buffer which holds the string. The char buffer must
     * be treated as read only! The position of the char buffer denotes
     * the beginning of the string when this method is invoked. After 
     * completion of this method, the position is set after the last consumed
     * character.
     * @return The value. Nullable. 
     *
     * @throws ParseException if conversion failed. The error offset field is
     * set to the position where parsing failed. The position of the buffer
     * is undefined.
     * @throws java.io.IOException Thrown by the CharBuffer.
     */
    T fromString(CharBuffer in) throws ParseException, IOException;
    
    /**
     * Constructs a value from a String.
     * <p>
     * The conversion only succeeds if the entire string is consumed.
     * <p>
     * This method does not change the state of the converter. 
     * <p>
     * Note: this is a convenience method. Implementing classes rarely need
     * to overwrite this method.
     *
     * @param in The String.
     * @return The value. Nullable.
     *
     * @throws ParseException if conversion failed.
     */
    default T fromString(CharSequence in) throws ParseException {
        CharBuffer buf = CharBuffer.wrap(in);
        T value;
        try {
            value = fromString(buf);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        if (buf.remaining()!=0) {
            throw new ParseException(buf.remaining()+" remaining character(s) not consumed.",buf.position());
        }
        return value;
    }
}
