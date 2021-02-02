/*
 * @(#)PathBuilderException.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

/**
 * PathBuilderException.
 *
 * @author Werner Randelshofer
 */
public class PathBuilderException extends Exception {

    private final static long serialVersionUID = 0L;

    public PathBuilderException(String message) {
        super(message);
    }

    public PathBuilderException(Exception cause) {
        super(cause);
    }

    public PathBuilderException(String message, Exception cause) {
        super(message, cause);
    }

}
