/* @(#)StreamCssTokenizer.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.CharSequenceReader;

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
import static org.jhotdraw8.css.CssTokenType.TT_URL;

/**
 * {@code StreamCssTokenizer} processes an input stream of characters into tokens for
 * the {@code CssParser}.
 * <p>
 * The tokenizer implements the ISO 14977 EBNF productions listed below. Only
 * productions with all caps names are returned as tokens. Productions with
 * lowercase names are used as internal macros.
 * <p>
 * The tokenizer uses {@code CssScanner} for preprocessing the input stream. The
 * preprocessed input stream does not contain the following characters: \000,
 * \r, \f.
 * <pre>
 * IDENT       = ident ;
 * AT_KEYWORD  = "@" , ident ;
 * STRING      = string ;
 * BAD_STRING  = badstring ;
 * BAD_URI     = baduri ;
 * BAD_COMMENT = badcomment ;
 * HASH        = '#' , name ;
 * NUMBER      = num
 * PERCENTAGE  = num , '%' ;
 * DIMENSION   = num , ident ;
 * URI         = ( "url(" , w , string , w , ')'
 *               | "url(" , w, { urichar | nonascii | escape }-, w, ')'
 *               )
 * UNICODE_RANGE = "u+", ( hexd, 5 * [ hexd ]
 *                       | hexd, 5 * [ hexd ] [ '-', hexd, 5 * [ hexd ] ]
 *                       | [ hexd, 4 * [ hexd ] ] , '?' , 5 * [ '?' ]
 *                       ) ;
 * CDO           = "&lt;!--" ;
 * CDC           = "--&gt;" ;
 * :             = ':' ;
 * ;             = ';' ;
 * {             = '{' ;
 * }             = '}' ;
 * (             = '(' ;
 * )             = ')' ;
 * [             = '[' ;
 * ]             = ']' ;
 * S             = { w }- ;
 * COMMENT       = '/', '*' , { ? anything but '*' followed by '/' ? } , '*', '/' ;
 * FUNCTION      = ident , '(' ;
 * INCLUDE_MATCH = '~', '=' ;
 * DASH_MATCH    = '|', '=' ;
 * PREFIX_MATCH  = '^', '=' ;
 * SUFFIX_MATCH  = '$', '=' ;
 * SUBSTRING_MATCH
 *               = '*', '=' ;
 * COLUMN        = '|', '|' ;
 * DELIM         = ? any other character not matched by the above rules,
 *                   and neither a single nor a double quote ? ;
 *
 * ident         = [ '-' ] , nmstart , { nmchar } ;
 * name          = { nmchar }- ;
 * nmstart       = '_' | letter | nonascii | escape ;
 * nonascii      = ? U+00A0 through U+10FFFF ? ;
 * letter        = ? 'a' through 'z' or 'A' through 'Z' ?
 * unicode       = '\' , ( 6 * hexd
 *                       | hexd , 5 * [hexd] , w
 *                       );
 * escape        = ( unicode
 *                 | '\' , -( newline | hexd)
 *                 ) ;
 * nmchar        = '_' | letter | digit | '-' | nonascii | escape ;
 * num           = [ '+' | '-' ] ,
 *                 ( { digit }-
 *                 | { digit } , '.' , { digit }-
 *                 )
 *                 [ 'e'  , [ '+' | '-' ] , { digit }- ] ;
 * digit         = ? '0' through '9' ?
 * letter        = ? 'a' through 'z' ? | ? 'A' through 'Z' ? ;
 * string        = string1 | string2 ;
 * string1       = '"' , { -( newline | '"' ) | '\\' , newline |  escape } , '"' ;
 * string2       = "'" , { -( newline | "'" ) | '\\' , newline |  escape } , "'" ;
 * badstring     = badstring1 | badstring2 ;
 * badstring1    = '"' , { -( newline | '"' ) | '\\' , newline |  escape } ;
 * badstring2    = "'" , { -( newline | "'" ) | '\\' , newline |  escape } ;
 * badcomment    = badcomment1 | badcomment2 ;
 * badcomment1   = '/' , '*' , { ? anything but '*' followed by '/' ? } , '*' ;
 * badcomment2   = '/' , '*' , { ? anything but '*' followed by '/' ? } ;
 * baduri        = baduri1 | baduri2 | baduri3 ;
 * baduri1       = "url(" , w , { urichar | nonascii | escape } , w ;
 * baduri2       = "url(" , w , string, w ;
 * baduri3       = "url(" , w , badstring ;
 * newline       = '\n' ;
 * w             = { ' ' | '\t' | newline } ;
 * urichar       = '!' | '#' | '$' | '%' | '&amp;' | ? '*' through '[' ?
 *                 | ? ']' through '~' ? ;
 * hexd           = digit | ? 'a' through 'f' ? | ? 'A' through 'F' ? ;
 * </pre>
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">CSS Syntax
 * Module Level 3, Chapter 4. Tokenization</a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StreamCssTokenizer implements CssTokenizer {

    private CssScanner in;

    private boolean pushBack;

    private int currentToken;

    @Nullable
    private String stringValue;
    @Nullable
    private Number numericValue;
    private int lineNumber;
    private int startPosition;
    private int endPosition;

    public StreamCssTokenizer(CharBuffer charBuffer) {
        this(new CharBufferReader(charBuffer));
    }

    public StreamCssTokenizer(CharSequence charSequence) {
        this(new CharSequenceReader(charSequence));
    }

    public StreamCssTokenizer(Reader reader) {
        in = new CssScanner(reader);
    }

    @Override
    public int current() {
        return currentToken;
    }

    @Nullable
    @Override
    public String currentString() {
        return stringValue;
    }


    @Nullable
    @Override
    public Number currentNumber() {
        return numericValue;
    }

    @Override
    public int next() throws IOException {
        do {
            nextNoSkip();
        } while ( currentToken == TT_COMMENT || currentToken == TT_BAD_COMMENT//
                || currentToken == TT_S || currentToken == TT_CDC || currentToken == TT_CDO);
        return currentToken;
    }

    @Override
    public int nextNoSkip() throws IOException {
        if (pushBack) {
            pushBack = false;
            return currentToken;
        }

        lineNumber = (int) in.getLineNumber();
        startPosition = (int) in.getPosition();
        endPosition = (int) in.getPosition();

        int ch = in.nextChar();
        stringValue = null;
        numericValue = null;
        switch (ch) {
            case -1:  // EOF
                currentToken = TT_EOF;
                stringValue = "<EOF>";
                break;
            case ' ':
            case '\n':
            case '\t': {
                StringBuilder buf = new StringBuilder();
                while (ch == ' ' || ch == '\n' || ch == '\t') {
                    buf.append((char) ch);
                    ch = in.nextChar();
                }
                in.pushBack(ch);
                currentToken = TT_S;
                stringValue = buf.toString();
                break;
            }
            case '~': {
                int next = in.nextChar();
                if (next == '=') {
                    currentToken = TT_INCLUDE_MATCH;
                    stringValue = "~=";
                } else {
                    in.pushBack(next);
                    currentToken = '~';
                    stringValue = String.valueOf((char) ch);
                }
                break;
            }
            case '|': {
                int next = in.nextChar();
                if (next == '=') {
                    currentToken = TT_DASH_MATCH;
                    stringValue = "|=";
                } else if (next == '|') {
                    currentToken = TT_COLUMN;
                    stringValue = "||";
                } else {
                    in.pushBack(next);
                    currentToken = '|';
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
            case '^': {
                int next = in.nextChar();
                if (next == '=') {
                    currentToken = TT_PREFIX_MATCH;
                    stringValue = "^=";
                } else {
                    in.pushBack(next);
                    currentToken = '^';
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
            case '$': {
                int next = in.nextChar();
                if (next == '=') {
                    currentToken = TT_SUFFIX_MATCH;
                    stringValue = "$=";
                } else {
                    in.pushBack(next);
                    currentToken = '$';
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
            case '*': {
                int next = in.nextChar();
                if (next == '=') {
                    currentToken = TT_SUBSTRING_MATCH;
                    stringValue = "*=";
                } else {
                    in.pushBack(next);
                    currentToken = '*';
                    stringValue = String.valueOf((char) ch);
                }
                break;
            }
            case '@': {
                StringBuilder buf = new StringBuilder();
                if (identMacro(ch = in.nextChar(), buf)) {
                    currentToken = TT_AT_KEYWORD;
                    stringValue = buf.toString();
                } else {
                    in.pushBack(ch);
                    currentToken = '@';
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
            case '#': {
                StringBuilder buf = new StringBuilder();
                if (nameMacro(ch = in.nextChar(), buf)) {
                    currentToken = TT_HASH;
                    stringValue = buf.toString();
                } else {
                    in.pushBack(ch);
                    currentToken = '#';
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
            case '\'':
            case '"': {
                StringBuilder buf = new StringBuilder();
                if (stringMacro(ch, buf)) {
                    currentToken = TT_STRING;
                    stringValue = buf.toString();
                } else {
                    currentToken = TT_BAD_STRING;
                    stringValue = buf.toString();
                }
                break;

            }
            case '+':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                StringBuilder buf = new StringBuilder();
                StringBuilder unitBuf = new StringBuilder();
                if (numMacro(ch, buf)) {
                    ch = in.nextChar();
                    if (ch == '%') {
                        currentToken = TT_PERCENTAGE;
                        stringValue = "%";
                    } else if (identMacro(ch, unitBuf)) {
                        currentToken = TT_DIMENSION;
                        stringValue = unitBuf.toString();
                    } else {
                        in.pushBack(ch);
                        currentToken = TT_NUMBER;
                    }
                } else {
                    currentToken = ch;
                    stringValue = String.valueOf((char) currentToken);
                }
                break;

            }
            case '/': {
                int next = in.nextChar();
                if (next == '*') {
                    StringBuilder buf = new StringBuilder();
                    if (commentAfterSlashStarMacro(buf)) {
                        currentToken = TT_COMMENT;
                    } else {
                        currentToken = TT_BAD_COMMENT;
                    }
                    stringValue = buf.toString();
                } else {
                    in.pushBack(next);
                    currentToken = ch;
                    stringValue = String.valueOf((char) currentToken);
                }
                break;

            }
            case '-': {
                int next1 = in.nextChar();
                if (next1 == '-') {
                    int next2 = in.nextChar();
                    if (next2 == '>') {
                        stringValue = "-->";
                        currentToken = TT_CDC;
                    } else {
                        StringBuilder buf=new StringBuilder();
                        buf.append("--");
                        if (nameMacro(next2,buf)) {
                            currentToken=TT_IDENT;
                            stringValue=buf.toString();
                        } else {
                            in.pushBack(next2);
                            in.pushBack(next1);
                            currentToken = ch;
                            stringValue = String.valueOf((char) currentToken);
                        }
                    }
                } else {
                    in.pushBack(next1);
                    StringBuilder buf = new StringBuilder();
                    StringBuilder unitBuf = new StringBuilder();
                    if (numMacro(ch, buf)) {
                        ch = in.nextChar();
                        if (ch == '%') {
                            currentToken = TT_PERCENTAGE;
                            stringValue = "%";
                        } else if (identMacro(ch, unitBuf)) {
                            currentToken = TT_DIMENSION;
                            stringValue = unitBuf.toString();
                        } else {
                            in.pushBack(ch);
                            currentToken = TT_NUMBER;
                        }
                    } else {
                        if (identMacro(ch, buf)) {
                            next1 = in.nextChar();
                            if (next1 == '(') {
                                currentToken = TT_FUNCTION;
                            } else {
                                in.pushBack(next1);
                                currentToken = TT_IDENT;
                            }
                            stringValue = buf.toString();
                        } else {
                            currentToken = ch;
                            stringValue = String.valueOf((char) currentToken);
                        }
                    }
                }
                break;
            }
            case '<': {
                int next1 = in.nextChar();
                if (next1 == '!') {
                    int next2 = in.nextChar();
                    if (next2 == '-') {
                        int next3 = in.nextChar();
                        if (next3 == '-') {
                            stringValue = "<!--";
                            currentToken = TT_CDO;
                        } else {
                            in.pushBack(next3);
                            in.pushBack(next2);
                            in.pushBack(next1);
                            currentToken = ch;
                            stringValue = String.valueOf((char) currentToken);
                        }
                    } else {
                        in.pushBack(next2);
                        in.pushBack(next1);
                        StringBuilder buf = new StringBuilder();
                        currentToken = ch;
                        stringValue = String.valueOf((char) currentToken);
                    }
                } else {
                    in.pushBack(next1);
                    StringBuilder buf = new StringBuilder();
                    currentToken = ch;
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
            case 'u':
            case 'U': {
                // FIXME implement UNICODE_RANGE token

                StringBuilder buf = new StringBuilder();
                if (identMacro(ch, buf)) {
                    int next1 = in.nextChar();
                    if (next1 == '(') {
                        stringValue = buf.toString();
                        if (stringValue.equalsIgnoreCase("url")) {
                            buf.setLength(0);
                            if (uriMacro(buf)) {
                                currentToken = TT_URL;
                            } else {
                                currentToken = TT_BAD_URI;
                            }
                            stringValue = buf.toString();
                        } else {
                            currentToken = TT_FUNCTION;
                        }
                    } else {
                        in.pushBack(next1);
                        currentToken = TT_IDENT;
                        stringValue = buf.toString();
                    }
                } else {
                    currentToken = ch;
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }

            default: {
                StringBuilder buf = new StringBuilder();
                if (identMacro(ch, buf)) {
                    int next1 = in.nextChar();
                    if (next1 == '(') {
                        stringValue = buf.toString();
                        currentToken = TT_FUNCTION;
                    } else {
                        in.pushBack(next1);
                        currentToken = TT_IDENT;
                        stringValue = buf.toString();
                    }
                } else {
                    currentToken = ch;
                    stringValue = String.valueOf((char) currentToken);
                }
                break;
            }
        }
        endPosition = (int) in.getPosition();
        return currentToken;
    }

    /**
     * Pushes the current token back.
     */
    @Override
    public void pushBack() {
        pushBack = true;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 'ident' macro.
     *
     * @param ch current character
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean identMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        boolean consumed = false;
        if (ch == '-') {
            buf.append('-');
            consumed = true;
            ch = in.nextChar();
        }

        if (nmstartMacro(ch, buf)) {
            while (nmcharMacro(ch = in.nextChar(), buf)) {
            }
            in.pushBack(ch);
            return true;
        } else {
            if (consumed) {
                in.pushBack(ch);
            }
            return false;
        }
    }

    /**
     * 'name' macro.
     *
     * @param ch current character
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean nameMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        if (nmcharMacro(ch, buf)) {
            while (nmcharMacro(ch = in.nextChar(), buf)) {
            }
            in.pushBack(ch);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 'num' macro.
     *
     * @param ch current character
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean numMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        boolean hasSign = false;
        if (ch == '-') {
            hasSign = true;
            buf.append('-');
            ch = in.nextChar();
        } else if (ch == '+') {
            hasSign = true;
            buf.append('+');
            ch = in.nextChar();
        }

        boolean hasDecimals = false;
        boolean hasFractionalsOrExponent = false;
        while ('0' <= ch && ch <= '9') {
            hasDecimals = true;
            buf.append((char) ch);
            ch = in.nextChar();
        }
        if (ch == '.') {
            hasFractionalsOrExponent = true;
            int next = in.nextChar();
            if (!('0' <= next && next <= '9')) {
                in.pushBack(next);
                if (hasDecimals) {
                    in.pushBack(ch);
                    numericValue = Long.parseLong(buf.toString());
                    return true;
                }
                return false;
            }
            buf.append('.');
            ch = next;
            while ('0' <= ch && ch <= '9') {
                buf.append((char) ch);
                ch = in.nextChar();
            }
        }

        if ((hasDecimals || hasFractionalsOrExponent) && (ch == 'e' || ch == 'E')) {
            hasFractionalsOrExponent = true;
            buf.append('E');
            int expchar = ch;
            ch = in.nextChar();

            if (ch == '-') {
                buf.append('-');
                ch = in.nextChar();
            } else if (ch == '+') {
                ch = in.nextChar();
            }
            boolean hasExponents = false;
            while ('0' <= ch && ch <= '9') {
                hasExponents = true;
                buf.append((char) ch);
                ch = in.nextChar();
            }
            if (!hasExponents) {
                in.pushBack(ch);
                ch = expchar;
                buf.setLength(buf.length() - 1);
            }
        }

        if (!hasDecimals && !hasFractionalsOrExponent) {
            if (hasSign) {
                in.pushBack(ch);
                buf.setLength(buf.length() - 1);
            }
            return false;
        }

        try {
            if (hasFractionalsOrExponent) {
                numericValue = Double.parseDouble(buf.toString());
            } else {
                numericValue = Long.parseLong(buf.toString());
            }
        } catch (NumberFormatException e) {
            throw new InternalError("Tokenizer is broken.", e);
        }

        in.pushBack(ch);
        return true;
    }

    /**
     * 'nmstart' macro.
     *
     * @param ch current character
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean nmstartMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        if (ch == '_' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z') {
            buf.append((char) ch);
            return true;
        } else if (ch > 159) {
            buf.append((char) ch);
            return true;
        } else if (ch == '\\') {
            return escapeMacro(ch, buf);
        }

        return false;
    }

    /**
     * 'escape' macro.
     *
     * @param ch current character must be a backslash
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean escapeMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        if (ch == '\\') {
            ch = in.nextChar();
            if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f' || 'A' <= ch && ch <= 'F') {
                return unicodeAfterBackslashMacro(ch, buf);
            } else if (ch == '\n') {
                in.pushBack(ch);
                return false;
            } else {
                buf.append((char) ch);
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a hexadecimal character to an integer.
     *
     * @param ch a character
     * @return A value between 0 and 15. Returns -1 if ch is not a hex digit
     * character.
     */
    private int hexToInt(int ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        } else if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        } else if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        return -1;
    }

    /**
     * 'unicode' macro.
     *
     * @param ch current character must be the first character after the
     * backslash
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean unicodeAfterBackslashMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        int unicodeScalar = hexToInt(ch);
        if (unicodeScalar == -1) {
            return false;
        }
        int count = 1;
        for (int digit = hexToInt(ch = in.nextChar()); digit != -1 && count < 6; digit = hexToInt(ch = in.nextChar())) {
            unicodeScalar = (unicodeScalar << 4) | digit;
            count++;
        }

        if (count < 6) { // => could be followed by whitespace
            switch (ch) {
                case ' ':
                case '\t':
                case '\n': // linebreaks are preprocssed by scanner
                    // consume char
                    break;
                default:
                    in.pushBack(ch);
            }
        } else {
            in.pushBack(ch);
        }

        ch = unicodeScalar;
        if (!(0 <= ch && ch <= 0xd7ff || 0xe000 <= ch && ch <= 0x110000)) {
            // => illegal unicode scalar
            ch = 0xfffd; // assign replacement character
        }
        if (!(ch < 0x10000)) {
            // => unicode scalar must be encoded with a surrogate pair
            //    UTF-32: 000uuuuuxxxxxxyyyyyyyyyy
            //                    1   0   0   0
            //
            //    subtract offset 0x10000: (Note: wwww = uuuuu - 1).
            //            0000wwwwxxxxxxyyyyyyyyyy
            //       
            //    UTF-16: 110110_wwww_xxxxxx 110111_yy_yyyyyyyy
            //            
            int wxy = ch - 0x10000;
            int high = 0b110110_0000_000000 | (wxy >> 10);
            int low = 0b110111_0000000000 | (wxy & 0b11_11111111);
            buf.append((char) high);
            buf.append((char) low);
        } else {
            buf.append((char) ch);
        }
        return true;
    }

    /**
     * 'nmchar' macro.
     *
     * @param ch current character
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean nmcharMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        if (ch == '_' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z'//
                || '0' <= ch && ch <= '9' || ch == '-') {
            buf.append((char) ch);
            return true;
        } else if (ch > 159) {
            buf.append((char) ch);
            return true;
        } else if (ch == '\\') {
            return escapeMacro(ch, buf);
        }
        return false;
    }

    /**
     * 'comment' macro. SlashStar must have been consumed.
     *
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean commentAfterSlashStarMacro(@Nonnull StringBuilder buf) throws IOException {
        int ch = in.nextChar();
        while (ch != -1) {
            if (ch == '*') {
                ch = in.nextChar();
                if (ch == '/') {
                    return true;
                }
                buf.append('*');
            } else {
                buf.append((char) ch);
                ch = in.nextChar();
            }
        }
        return false;
    }

    /**
     * 'uri' macro.
     *
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean uriMacro(@Nonnull StringBuilder buf) throws IOException {
        int ch = in.nextChar();
        // skip whitespace
        while (ch == ' ' || ch == '\n' || ch == '\t') {
            ch = in.nextChar();
        }
        if (ch == '\'' || ch == '"') {
            // consume string
            if (!stringMacro(ch, buf)) {
                return false;
            }
            ch = in.nextChar();
        } else {
            while (true) {
                if (ch == '!' || ch == '#' || ch == '$' || ch == '%' || ch == '&'//
                        || '*' <= ch && ch <= '[' || ']' <= ch && ch <= '~' || ch > 159) {
                    // consume ascii url char or nonascii char
                    buf.append((char) ch);
                } else if (ch == '\'') {
                    // try to consume macro
                    if (!escapeMacro(ch, buf)) {
                        break;
                    }
                } else {
                    break;
                }
                ch = in.nextChar();
            }
        }
        // skip whitespace
        while (ch == ' ' || ch == '\n' || ch == '\t') {
            ch = in.nextChar();
        }
        if (ch == ')') {
            return true;
        }
        in.pushBack(ch);
        return false;
    }

    /**
     * 'string' macro.
     *
     * @param ch current character, must be a quote character
     * @param buf the token that we are currently building
     * @return true on success
     */
    private boolean stringMacro(int ch, @Nonnull StringBuilder buf) throws IOException {
        int quote = ch;
        if (quote != '\'' && quote != '"') {
            throw new IllegalArgumentException("illegal quote character:" + (char) ch);
        }
        while (true) {
            ch = in.nextChar();
            if (ch == -1) {
                return false;
            } else if (ch == '\\') {
                if (!escapeMacro(ch, buf)) {
                    int nextch = in.nextChar();
                    if (nextch == '\n') {
                        buf.append('\n');
                    } else {
                        in.pushBack(nextch);
                        in.pushBack(ch);
                        return false;
                    }
                }
            } else if (ch == '\n') {
                in.pushBack(ch);
                return false;
            } else if (ch == quote) {
                return true;
            } else {
                buf.append((char) ch);
            }
        }
    }

    @Override
    public int getStartPosition() {
        return startPosition;
    }

    @Override
    public int getEndPosition() {
        return endPosition;
    }
    /**
     * Gets the current position.
     *
     * @return the start position of the token if a token has been pushed back,
     * the end position of the token otherwise
     */
    public int getNextPosition() {
        return pushBack ? startPosition : endPosition;
    }


    private void skipWhitespace() throws IOException {
        while (nextNoSkip() == TT_S//
                || currentToken == TT_CDC//
                || currentToken == TT_CDO) {
        }
        pushBack();
    }

    @Override
    public CssToken getToken() {
        return new CssToken(currentToken,stringValue,numericValue,lineNumber,startPosition,endPosition);
    }
}
