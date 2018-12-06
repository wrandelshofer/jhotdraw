/* @(#)CssToken.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * CssToken.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssToken /*extends AST*/ {

    /**
     * The token type.
     */
    private final int ttype;
    /**
     * The string value.
     */
    private final String stringValue;
    /**
     * The numeric value.
     */
    private final Number numericValue;

    private final int startPos;
    private final int endPos;
    private final int lineNumber;

    @Nullable
    private final Character preferredQuoteChar;

    private final static XmlNumberConverter NUMBER_CONVERTER = new XmlNumberConverter();

    public CssToken(int ttype, String stringValue) {
        this(ttype, stringValue, null, 0, 0, stringValue.length());

    }

    public CssToken(int ttype, String stringValue, @Nullable Character preferredQuoteChar) {
        this(ttype, stringValue, null, preferredQuoteChar, 0, stringValue.length());

    }

    public CssToken(int ttype) {
        this(ttype, Character.toString((char) ttype), null, null, 0, 0, 1);

    }

    public CssToken(int ttype, Number numericValue, String stringValue) {
        this(ttype, stringValue, numericValue, null, 0, 0, 1);
    }

    public CssToken(int ttype, Number numericValue) {
        this(ttype, "", numericValue, null, 0, 0, 1);
    }

    public CssToken(int ttype, String stringValue, Number numericValue, int lineNumber, int startPos, int endPos) {
        this(ttype, stringValue, numericValue, null, lineNumber, startPos, endPos);
    }

    public CssToken(int ttype, String stringValue, Number numericValue, @Nullable Character preferredQuoteChar, int lineNumber, int startPos, int endPos) {
        this.ttype = ttype;
        this.stringValue = stringValue;
        this.numericValue = numericValue;
        this.lineNumber = lineNumber;
        this.startPos = startPos;
        this.endPos = endPos;
        this.preferredQuoteChar = preferredQuoteChar;
    }

    @Override
    public String toString() {
        return fromToken();
    }

    public String fromToken() {
        if (ttype >= 0) {
            return stringValue;
        }
        switch (ttype) {
            case CssTokenType.TT_IDENT:
                return fromIDENT();
            case CssTokenType.TT_AT_KEYWORD:
                return fromHASHorAT('@', stringValue);
            case CssTokenType.TT_STRING:
                return fromSTRING();
            case CssTokenType.TT_BAD_STRING:
                return fromBAD_STRING();
            //case CssTokenType.TT_BAD_URI : return fromBAD_URI(stringValue) ;
            //case CssTokenType.TT_BAD_COMMENT : return fromBAD_COMMENT(stringValue) ;
            case CssTokenType.TT_HASH:
                return fromHASHorAT('#', stringValue);
            case CssTokenType.TT_NUMBER:
                return fromNUMBER();
            case CssTokenType.TT_PERCENTAGE:
                return fromPERCENTAGE();
            case CssTokenType.TT_DIMENSION:
                return fromDIMENSION();
            case CssTokenType.TT_URL:
                return fromURL();
            case CssTokenType.TT_UNICODE_RANGE:
                return fromUNICODE_RANGE();
            case CssTokenType.TT_CDO:
                return fromCDO();
            case CssTokenType.TT_CDC:
                return fromCDC();
            case CssTokenType.TT_S:
                return fromS();
            case CssTokenType.TT_COMMENT:
                return fromCOMMENT();
            case CssTokenType.TT_FUNCTION:
                return fromIDENT() + "(";
            case CssTokenType.TT_INCLUDE_MATCH:
                return fromINCLUDE_MATCH();
            case CssTokenType.TT_DASH_MATCH:
                return fromDASH_MATCH();
            case CssTokenType.TT_PREFIX_MATCH:
                return fromPREFIX_MATCH();
            case CssTokenType.TT_SUFFIX_MATCH:
                return fromSUFFIX_MATCH();
            case CssTokenType.TT_SUBSTRING_MATCH:
                return fromSUBSTRING_MATCH();
            case CssTokenType.TT_COLUMN:
                return fromCOLUMN();
            case CssTokenType.TT_EOF:
                return "<EOF>";

        }
        throw new InternalError("Unsupported TTYPE:" + ttype);
    }

    private String fromCDC() {
        return "<!--";
    }

    private String fromCDO() {
        return "-->";
    }

    private String fromIDENT() {
        return fromIDENT(stringValue);
    }

    private String fromIDENT(@Nonnull String value) {
        StringBuilder out = new StringBuilder();
        Reader r = new StringReader(value);
        try {
            int ch = r.read();

            // identifier may start with zero or more '-'
            while (ch == '-') {
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
                if (ch == '_'
                        || 'a' <= ch && ch <= 'z'
                        || 'A' <= ch && ch <= 'Z'
                        || '0' <= ch && ch <= '9'
                        || ch == '-'
                        || 0xA0 <= ch && ch <= 0x10FFFF) {
                    out.append((char) ch);
                } else {
                    out.append('\\');
                    out.append((char) ch);
                }
            }
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("unexpected IO exception", e);
        }
    }

    private String fromHASHorAT(char hashOrAt, @Nonnull String value) {
        StringBuilder out = new StringBuilder();
        out.append(hashOrAt);
        Reader r = new StringReader(value);
        try {
            for (int ch = r.read(); ch != -1; ch = r.read()) {
                // escape nmchar if necessary
                if (ch == '_'
                        || 'a' <= ch && ch <= 'z'
                        || 'A' <= ch && ch <= 'Z'
                        || '0' <= ch && ch <= '9'
                        || ch == '-'
                        || 0xA0 <= ch && ch <= 0x10FFFF) {
                    out.append((char) ch);
                } else {
                    out.append('\\');
                    out.append((char) ch);
                }
            }
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("unexpected IO exception", e);
        }
    }


    private final static CssStringConverter cssStringConverter = new CssStringConverter();

    private String fromSTRING() {
        return fromSTRING(stringValue);
    }

    private String fromSTRING(String value) {
        char quoteChar =
                preferredQuoteChar != null
                        ? preferredQuoteChar
                        : value.indexOf('"') == -1 || value.indexOf('\'') == -1 ? '"' : '\'';
        return fromSTRING(value, quoteChar, quoteChar);
    }

    private String fromBAD_STRING() {
        return fromBAD_STRING(stringValue);
    }

    private String fromBAD_STRING(String value) {
        char quoteChar =
                preferredQuoteChar != null
                        ? preferredQuoteChar
                        : value.indexOf('"') == -1 || value.indexOf('\'') == -1 ? '"' : '\'';
        return fromSTRING(value, quoteChar, '\n');
    }

    private String fromSTRING(String value, final char firstQuoteChar, final char lastQuoteChar) {
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

    private String fromNUMBER() {
        return NUMBER_CONVERTER.toString(numericValue);
    }

    private String fromPERCENTAGE() {
        return Double.isFinite(numericValue.doubleValue()) ? fromNUMBER() + "%" : fromNUMBER();
    }

    private String fromDIMENSION() {
        return stringValue!=null&&Double.isFinite(numericValue.doubleValue()) ? fromNUMBER() + fromIDENT() : fromNUMBER();
    }

    private String fromURL() {
        StringBuilder out = new StringBuilder();
        out.append("url(");
        Reader r = new StringReader(stringValue);
        try {
            for (int ch = r.read(); ch != -1; ch = r.read()) {
                final boolean escape;
                switch (ch) {
                    case '"':
                    case '\'':
                    case '(':
                    case ')':
                    case '\\':
                        escape = true;
                        break;
                    default:
                        escape = Character.isWhitespace(ch) || Character.isISOControl(ch);
                        break;
                }
                if (escape) {
                    String hex = Integer.toHexString(ch);
                    out.append('\\');
                    out.append(hex);
                    if (hex.length() < 6) {
                        out.append(' ');
                    }
                } else {
                    out.append((char) ch);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("unexpected IO exception", e);
        }


        out.append(')');
        return out.toString();
    }

    private String fromUNICODE_RANGE() {
        return stringValue;
    }

    private String fromS() {
        return stringValue;
    }

    private String fromCOMMENT() {
        return '/' + '*' + stringValue.replace("*" + '/', "* /") + '*' + '/';
    }

    private String fromINCLUDE_MATCH() {
        return stringValue;
    }

    private String fromDASH_MATCH() {
        return stringValue;
    }

    private String fromPREFIX_MATCH() {
        return stringValue;
    }

    private String fromSUFFIX_MATCH() {
        return stringValue;
    }

    private String fromSUBSTRING_MATCH() {
        return stringValue;
    }

    private String fromCOLUMN() {
        return stringValue;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Number getNumericValue() {
        return numericValue;
    }

    public int getType() {
        return ttype;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
