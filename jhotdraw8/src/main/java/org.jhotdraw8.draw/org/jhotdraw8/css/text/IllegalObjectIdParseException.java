/*
 * @(#)IllegalObjectIdParseException.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.text.ParseException;

/**
 * This parse exception can be thrown to indicate that an object id is illegal.
 */
public class IllegalObjectIdParseException extends ParseException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ParseException with the specified detail message and
     * offset.
     * A detail message is a String that describes this particular exception.
     *
     * @param s           the detail message
     * @param errorOffset the position where the error is found while parsing.
     */
    public IllegalObjectIdParseException(String s, int errorOffset) {
        super(s, errorOffset);
    }
}
