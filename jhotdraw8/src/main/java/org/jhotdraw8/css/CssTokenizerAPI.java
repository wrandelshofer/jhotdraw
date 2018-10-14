/* @(#)CssTokenizerAPI.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.text.ParseException;
import javax.annotation.Nullable;

import static org.jhotdraw8.css.CssTokenType.TT_AT_KEYWORD;
import static org.jhotdraw8.css.CssTokenType.TT_BAD_COMMENT;
import static org.jhotdraw8.css.CssTokenType.TT_BAD_STRING;
import static org.jhotdraw8.css.CssTokenType.TT_BAD_URI;
import static org.jhotdraw8.css.CssTokenType.TT_CDC;
import static org.jhotdraw8.css.CssTokenType.TT_CDO;
import static org.jhotdraw8.css.CssTokenType.TT_COLUMN;
import static org.jhotdraw8.css.CssTokenType.TT_COMMENT;
import static org.jhotdraw8.css.CssTokenType.TT_DASH_MATCH;
import static org.jhotdraw8.css.CssTokenType.TT_DIMENSION;
import static org.jhotdraw8.css.CssTokenType.TT_EOF;
import static org.jhotdraw8.css.CssTokenType.TT_FUNCTION;
import static org.jhotdraw8.css.CssTokenType.TT_HASH;
import static org.jhotdraw8.css.CssTokenType.TT_IDENT;
import static org.jhotdraw8.css.CssTokenType.TT_INCLUDE_MATCH;
import static org.jhotdraw8.css.CssTokenType.TT_NUMBER;
import static org.jhotdraw8.css.CssTokenType.TT_PERCENTAGE;
import static org.jhotdraw8.css.CssTokenType.TT_PREFIX_MATCH;
import static org.jhotdraw8.css.CssTokenType.TT_S;
import static org.jhotdraw8.css.CssTokenType.TT_STRING;
import static org.jhotdraw8.css.CssTokenType.TT_SUBSTRING_MATCH;
import static org.jhotdraw8.css.CssTokenType.TT_SUFFIX_MATCH;
import static org.jhotdraw8.css.CssTokenType.TT_UNICODE_RANGE;
import static org.jhotdraw8.css.CssTokenType.TT_URL;

/**
 * Defines the API of a CSS Tokenizer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CssTokenizerAPI {


    /** Returns the current value converted to a string.
     * The returned value can be used for String comparisons of the value.
     * @return the current value
     */
    @javax.annotation.Nullable
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
            case TT_URL:
                return currentStringValue();
            default:
                throw new AssertionError("unsupported token type "+ currentToken());
        }
    }

    @Nullable
    Number currentNumericValue();

    /** Returns the current string value.
     * @return  the current string value */
    @Nullable
    String currentStringValue();
    
    /** Returns the current token type.
     * @return the current token type */
    int currentToken();

    int getLineNumber();

    int getStartPosition();

    int getEndPosition();

    int nextToken() throws IOException;

    /**
     * Fetches the next token and throws a parse exception if it
     * is not of the required type.
     *
     * @param ttype the required token type
     * @param message the error message
     * @throws ParseException if the token is not of the required type
     * @throws IOException on IO exception
     */
    default void requireNextToken(int ttype, String message) throws ParseException,IOException {
        if (nextToken()!=ttype) {
            throw new ParseException(message,getStartPosition());
        }
    }

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
