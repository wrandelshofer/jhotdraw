package org.jhotdraw8.css;

import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Defines CSS 3 token types.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://www.w3.org/TR/css-syntax-3/#tokenization">
 * CSS Syntax Module Level 3, Tokenization</a></li>
 * </ul>
 * </p>
 */
public class CssToken {

    public final static String IDENT_NONE = "none";
    public final static String IDENT_INITIAL_VALUE = "initial-value";

    /**
     * Defines an at-keyword-token.
     * <pre>
     *     at-keyword-token = '@', ident-token ;
     * </pre>
     */
    public final static int TT_AT_KEYWORD = -3;

    /**
     * Defines a bad-comment-token.
     */
    public final static int TT_BAD_COMMENT = -7;

    /**
     * Defines a bad-string-token.
     */
    public final static int TT_BAD_STRING = -5;

    /**
     * Defines a bad-uri-token.
     */
    public final static int TT_BAD_URI = -6;

    /**
     * Defines a CDC-token.
     * <pre>
     *  CDC-token = "-->";
     * </pre>
     */
    public final static int TT_CDC = -15;

    /**
     * Defines a CDO-token.
     * <pre>
     *  CDO-token = "<!--";
     * </pre>
     */
    public final static int TT_CDO = -14;

    /**
     * Defines a column-token.
     * <pre>
     *  column-token = "||";
     * </pre>
     */
    public final static int TT_COLUMN = -24;

    /**
     * Defines a comment-token.
     * <pre>
     *     comment-token = "/','*',comment-body,'*','/' ;
     *     comment-body = (* anything but '*' followed by '/' *);
     * </pre>
     */
    public final static int TT_COMMENT = -17;

    /**
     * Defines a dash-match-token.
     * <pre>
     *  dash-match-token = "|=";
     * </pre>
     */
    public final static int TT_DASH_MATCH = -20;

    /**
     * Defines a dimension-token.
     * <pre>
     *     dimension-token = digit-token , ident-token ;
     * </pre>
     */
    public final static int TT_DIMENSION = -11;

    /**
     * Defines an EOF-token.
     */
    public final static int TT_EOF = -1;

    /**
     * Defines a function-token.
     * <pre>
     *     function-token = ident-token , '(' ;
     * </pre>
     */
    public final static int TT_FUNCTION = -18;

    /**
     * Defines a hash-token.
     * <pre>
     *     hash-token = '#' , ident-char , { ident-char } ;
     *
     *     ident-char = ( 'a'-'z' | 'A'-'Z' | '_' | '0'-'9' )
     *                | non-ASCII
     *                | escape ;
     *
     * </pre>
     */
    public final static int TT_HASH = -8;

    /**
     * Defines an ident-token.
     * <pre>
     *     ident-token = [ '-' ] , first-ident-char , { ident-char } ;
     *
     *     first-ident-char = ('a'-'z'|'A'-'Z'|'_')
     *                      | non-ASCII
     *                      | escape ;
     *
     *     ident-char = ( 'a'-'z' | 'A'-'Z' | '_' | '0'-'9' )
     *                | non-ASCII
     *                | escape ;
     *
     *     escape = '\' , (* not newline or hex-digit *)
     *            | '\' , 6 * hex-digit
     *            | '\', hex-digit , 4 * {hex-digit}, whitespace ;
     *
     *     hex-digit = '0'-'9'
     *               | 'a'-'f'
     *               | 'A'-'F' ;
     * </pre>
     */
    public final static int TT_IDENT = -2;

    /**
     * Defines an include-match-token.
     * <pre>
     *  include-match-token = "~=";
     * </pre>
     */
    public final static int TT_INCLUDE_MATCH = -19;

    /**
     * Defines a number-token.
     * <pre>
     *     number-token = [ '+' | '-' ] , mantissa , [exponent] ;
     *
     *     mantissa = digit, {digit} | {digit}, '.' digit, {digit} ;
     *
     *     exponent = ( 'e' | 'E' ), [ '+' , '-' ], digit , {digit} ;
     * </pre>
     */
    public final static int TT_NUMBER = -9;

    /**
     * Defines a percentage-token.
     * <pre>
     *     percentage-token = number-token , '%' ;
     * </pre>
     */
    public final static int TT_PERCENTAGE = -10;

    /**
     * Defines a prefix-match-token.
     * <pre>
     *  prefix-match-token = "^=";
     * </pre>
     */
    public final static int TT_PREFIX_MATCH = -21;

    /**
     * Defines a ws*-token.
     * <pre>
     *     ws* = { whitespace-token } ;
     *
     *     whitespace-token = { whitespace } ;
     *
     *     whitespace = ' ' | '\' | newline ;
     *
     *     newline = '\n' | "\r\n" | '\r' | '\f' ;
     * </pre>
     */
    public final static int TT_S = -16;

    /**
     * Defines a string-token.
     * <pre>
     *     string-token = quote-string
     *                  | apostrophe-string ;
     *
     *     quote-string = '"', {quote-string-body}, '"' ;
     *
     *     apostrophe-string = "'", {apostrophe-string-body}, "'" ;
     *
     *     quote-string-body = (* not " \ or newline | escape | '\' newline *);
     *
     *     apostrophe-string-body = (* not ' \ or newline | escape | '\' newline *);
     *
     *     escape = '\' , (* not newline or hex-digit *)
     *            | '\' , 6 * hex-digit
     *            | '\', hex-digit , 4 * {hex-digit}, whitespace ;
     *
     *     newline = '\n' | "\r\n" | '\r' | '\f' ;
     * </pre>
     *
     */
    public final static int TT_STRING = -4;

    /**
     * Defines a substring-match-token.
     * <pre>
     *  substring-match-token = "*=";
     * </pre>
     */
    public final static int TT_SUBSTRING_MATCH = -23;

    /**
     * Defines a suffix-match-token.
     * <pre>
     *  suffix-match-token = "$=";
     * </pre>
     */
    public final static int TT_SUFFIX_MATCH = -22;

    /**
     * Defines a unicode-range-token.
     * <pre>
     *     unicode-range = ('U'|'u'),'+',( mask-range, from-to-range );
     *     mask-range = 1 * hex-digit , 5 * { '?' }
     *                | 2 * hex-digit , 4 * { '?' }
     *                | 3 * hex-digit , 3 * { '?' }
     *                | 4 * hex-digit , 2 * { '?' }
     *                | 5 * hex-digit , 1 * { '?' }
     *                | 6 * hex-digit
     *     from-to-range = hex-digit, 5 * { hex-digit } , '-' , hex-digit, 5 * { hex-digit } ;
     * </pre>
     */
    public final static int TT_UNICODE_RANGE = -13;

    /**
     * Defines a url-token.
     * <pre>
     *     url-token = "url(" , ws* , url-unquoted | string-token , ws*, ')' ;
     *     url-unquoted = ur-unquoted-char , {url-unquoted-char} ;
     *     url-unquoted-char = not " ' ( ) \ whitespace or non-printable | escape ;
     * </pre>
     */
    public final static int TT_URL = -12;

    /**
     * Defines the comma delim-token.
     */
    public final static int TT_COMMA = ',';

    /**
     * Defines the semicolon delim-token.
     */
    public final static int TT_SEMICOLON = ';';

    /**
     * Defines the colon delim-token.
     */
    public final static int TT_COLON = ':';

    private final int type;
    @Nonnull
    private final String parsedValue;
    @Nonnull
    private final String dimension;
    private final Number parsedNumber;

    public CssToken( int type, @Nonnull Number parsedNumber, @Nonnull String dimension) {
        this.type = type;
        this.parsedNumber = parsedNumber;
        this.parsedValue = parsedNumber.toString();
        this.dimension = dimension;
    }

    public CssToken( int type, @Nonnull String parsedValue) {
        //noinspection ConstantConditions
        if (parsedValue == null || parsedValue.isEmpty()) {
            throw new IllegalArgumentException("parsedValue is null or empty. parsedValue=" + parsedValue);
        }
        this.type = type;
        this.parsedValue = parsedValue;
        this.parsedNumber = 0;
        this.dimension = "";
    }

    public CssToken(@Nonnull char charType) {
        this((int) charType, Character.toString(charType));
    }

    public int getType() {
        return type;
    }

    @Nonnull
    public String getParsedValue() {
        return parsedValue;
    }

    @Nonnull
    public String getDimension() {
        return dimension;
    }

    @Nonnull

    public String toCss() {
        switch (type) {
            case TT_AT_KEYWORD:
                return "@" + toCssIdent(parsedValue);
            case TT_BAD_COMMENT:
                return "/*" + parsedValue.replace("*/","* /") + "*/";
            case TT_BAD_STRING:
                return toBadCssString(parsedValue);
            case TT_BAD_URI:
                return "bad uri";
            case TT_CDC:
                return "<!--";
            case TT_CDO:
                return "-->";
            case TT_COLUMN:
                return "|";
            case TT_COMMENT:
                return "/*" + parsedValue.replace("*/","* /") + "*/";
            case TT_DASH_MATCH:
                return "|=";
            case TT_DIMENSION:
                return parsedValue + getDimension();
            case TT_EOF:
                return "";
            case TT_FUNCTION:
                return toCssIdent(parsedValue)+'(';
            case TT_HASH:
                return '#'+toCssIdent(parsedValue);
            case TT_IDENT:
                return toCssIdent(parsedValue);
            case TT_INCLUDE_MATCH:
                return "~=";
            case TT_NUMBER:
                return parsedValue;
            case TT_PERCENTAGE:
                return parsedValue + "%";
            case TT_PREFIX_MATCH:
                return "^=";
            case TT_S:
                return parsedValue;
            case TT_STRING:
                return toCssString(parsedValue);
            case TT_SUBSTRING_MATCH:
                return "*=";
            case TT_SUFFIX_MATCH:
                return "$=";
            case TT_UNICODE_RANGE:
                return parsedValue;
            case TT_URL:
                return parsedValue;
            default:
                return Character.toString((char) type);
        }
    }

    private String toCssString(String value) {
        char quoteChar = value.indexOf('"') == -1 || value.indexOf('\'') == -1 ? '"' : '\'';
        return toCssString(value, quoteChar, quoteChar);
    }
    private String toBadCssString(String value) {
        char quoteChar = value.indexOf('"') == -1 || value.indexOf('\'') == -1 ? '"' : '\'';
        return toCssString(value, quoteChar, '\n');
    }
    private String toCssString(String value, final char firstQuoteChar,final char lastQuoteChar) {
        StringBuilder out = new StringBuilder();
        out.append(firstQuoteChar);
        for (char ch : value.toCharArray()) {
            switch (ch) {
                case ' ':
                    out.append(ch);
                    break;
                case '\\':
                    out.append('\\');
                    out.append('\\');
                    break;
                case '\n':
                    out.append('\\');
                    out.append('\n');
                    break;
                default:
                    if (ch == firstQuoteChar) {
                        out.append('\\');
                        out.append(firstQuoteChar);
                    } else {

                        if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                            out.append('\\');
                            String hex = Integer.toHexString(ch);
                            for (int i = 0, n = 6 - hex.length(); i < n; i++) {
                                out.append('0');
                            }
                            out.append(hex);
                        } else {
                            out.append(ch);
                        }

                    }
                    break;
            }
        }
        out.append(lastQuoteChar);
        return out.toString();
    }

    private String toCssIdent(@Nonnull String value) {
        StringBuilder out = new StringBuilder();
        Reader r = new StringReader(value);
        try {
        int ch = r.read();

        // identifier may start with '-'
        if (ch == '-') {
            out.append((char) ch);
            ch = r.read();
        }

        if (ch == -1) {
            throw new IllegalArgumentException("nmstart missing! " + value);
        }

        // escape nmstart if necessary
        if (ch == '_'
                || 'a' <= ch && ch <= 'z'
                || 'A' <= ch && ch <= 'Z'
                || 0xA0 <= ch && ch <= 0x10FFFF) {
            out.append((char) ch);
        } else {
            switch (ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '\n':
                    String hex = Integer.toHexString(ch);
                    out.append('\\');
                    out.append(hex);
                    if (hex.length() < 6) {
                        out.append(' ');
                    }
                    break;
                default:
                    out.append('\\');
                    out.append((char) ch);
                    break;
            }
        }

        while (-1 != (ch = r.read())) {
            // escape nmchar if necessary
            if (ch == '_' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '-' || 0xA0 <= ch && ch <= 0x10FFFF) {
                out.append((char) ch);
            } else {
                out.append('\\');
                out.append((char) ch);
            }
        }
        return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("unexpected IO exception",e);
        }
    }

}
