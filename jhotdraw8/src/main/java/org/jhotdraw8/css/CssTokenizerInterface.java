/* @(#)CssTokenizerInterface.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * CssTokenizerInterface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CssTokenizerInterface {

    /**
     * Token types. DELIM token are given as UTF-16 characters.
     */
    int TT_AT_KEYWORD = -3;
    int TT_BAD_COMMENT = -7;
    int TT_BAD_STRING = -5;
    int TT_BAD_URI = -6;
    int TT_CDC = -15;
    int TT_CDO = -14;
    int TT_COLUMN = -24;
    int TT_COMMENT = -17;
    int TT_DASH_MATCH = -20;
    int TT_DIMENSION = -11;
    int TT_EOF = -1;
    int TT_FUNCTION = -18;
    int TT_HASH = -8;
    int TT_IDENT = -2;
    int TT_INCLUDE_MATCH = -19;
    int TT_NUMBER = -9;
    int TT_PERCENTAGE = -10;
    int TT_PREFIX_MATCH = -21;
    int TT_S = -16;
    int TT_STRING = -4;
    int TT_SUBSTRING_MATCH = -23;
    int TT_SUFFIX_MATCH = -22;
    int TT_UNICODE_RANGE = -13;
    int TT_URI = -12;

    /** Returns the current value converted to a string.
     * The returned value can be used for String comparisons of the value.
     * @return the current value
     */
    default String currentValue() {
        switch (currentToken()) {
            case TT_AT_KEYWORD:
                return "@" + currentStringValue();
            case TT_BAD_COMMENT:
                return "bad comment";
            case TT_BAD_STRING:
                return "bad string";
            case TT_BAD_URI:
                return "bad uri";
            case TT_CDC:
                return "<!--";
            case TT_CDO:
                return "-->";
            case TT_COLUMN:
                return "|";
            case TT_COMMENT:
                return currentStringValue();
            case TT_DASH_MATCH:
                return "|=";
            case TT_DIMENSION:
                return currentNumericValue() + currentStringValue();
            case TT_EOF:
                return "eof";
            case TT_FUNCTION:
                return currentStringValue();
            case TT_HASH:
                return currentStringValue();
            case TT_IDENT:
                return currentStringValue();
            case TT_INCLUDE_MATCH:
                return "~=";
            case TT_NUMBER:
                return "" + currentNumericValue();
            case TT_PERCENTAGE:
                return currentNumericValue() + "%";
            case TT_PREFIX_MATCH:
                return "^=";
            case TT_S:
                return " ";
            case TT_STRING:
                return currentStringValue();
            case TT_SUBSTRING_MATCH:
                return "*=";
            case TT_SUFFIX_MATCH:
                return "$=";
            case TT_UNICODE_RANGE:
                return currentStringValue();
            case TT_URI:
                return currentStringValue();
            default:
                return Character.toString((char) currentToken());
        }
    }

    @Nullable
    Number currentNumericValue();

    /** Returns the current string value.
     * @return  the current string value */
    @Nullable
    String currentStringValue();
    
    /** Returns the current token id.
     * @return the current token */
    int currentToken();

    int getLineNumber();

    int getStartPosition();

    int getEndPosition();

    int nextToken() throws IOException;

    /**
     * Pushes the current token back.
     */
    void pushBack();

    /**
     * Consumes tokens until a non-whitespace token arrives. That token is then
     * pushed back.
     *
     * @throws IOException on IO failure
     */
    void skipWhitespace() throws IOException;

    /**
     * Whether white spaces should be skipped.
     *
     * @param newValue new value
     */
    void setSkipWhitespaces(boolean newValue);

    /**
     * Whether comments should be skipped.
     *
     * @param newValue new value
     */
    void setSkipComments(boolean newValue);
}
