/* @(#)CssTokenizerAPI.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.text.ParseException;
import javax.annotation.Nonnull;
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
                return "@" + currentString();
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
                return currentString();
            case TT_DASH_MATCH:
                return "|=";
            case TT_DIMENSION:
                return currentNumber() + currentString();
            case TT_EOF:
                return "eof";
            case TT_FUNCTION:
                return currentString();
            case TT_HASH:
                return currentString();
            case TT_IDENT:
                return currentString();
            case TT_INCLUDE_MATCH:
                return "~=";
            case TT_NUMBER:
                return "" + currentNumber();
            case TT_PERCENTAGE:
                return currentNumber() + "%";
            case TT_PREFIX_MATCH:
                return "^=";
            case TT_S:
                return " ";
            case TT_STRING:
                return currentString();
            case TT_SUBSTRING_MATCH:
                return "*=";
            case TT_SUFFIX_MATCH:
                return "$=";
            case TT_UNICODE_RANGE:
                return currentString();
            case TT_URL:
                return currentString();
            default:
                throw new AssertionError("unsupported token type "+ currentToken());
        }
    }

    @Nullable
    Number currentNumber();

    @Nonnull
    default Number currentNumberNonnull() {
        Number number = currentNumber();
        if (number == null) {
            throw new AssertionError("currentNumber is null");
        }
        return number;
    }
    /** Returns the current string value.
     * @return  the current string value */
    @Nullable
    String currentString();

    @Nonnull
    default String currentStringNonnull() {
        String str = currentString();
        if (str == null) {
            throw new AssertionError("currentString is null");
        }
        return str;
    }
    
    /** Returns the current token type.
     * @return the current token type */
    int currentToken();

    int getLineNumber();

    int getStartPosition();

    int getEndPosition();
    /**
     * Gets the current position.
     *
     * @return the start position of the token if a token has been pushed back,
     * the end position of the token otherwise
     */
    int getNextPosition();


    int nextToken() throws IOException;

    default void skipIfPresent(int ttype) throws IOException {
            if (nextToken() != ttype) {
                pushBack();
            }
    }

    /**
     * Invokes {@link #nextToken()} and checks if it is a "none" identifier.
     *
     * @return true if "none"
     * @throws IOException on io error
     */
    default boolean nextTokenIsIdentNone() throws IOException {
        return nextToken()==CssTokenType.TT_IDENT&& currentStringNonnull().equals(CssTokenType.IDENT_NONE);
    }

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
