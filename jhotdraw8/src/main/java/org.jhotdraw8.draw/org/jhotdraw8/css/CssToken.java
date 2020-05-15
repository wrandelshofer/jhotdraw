/*
 * @(#)CssToken.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * CssToken.
 *
 * @author Werner Randelshofer
 */
public class CssToken /*extends AST*/ {

    /**
     * The token type.
     */
    private final int ttype;
    /**
     * The string value.
     */
    @Nullable
    private final String stringValue;
    /**
     * The numeric value.
     */
    @Nullable
    private final Number numericValue;

    private final int startPos;
    private final int endPos;
    private final int lineNumber;

    @Nullable
    private final Character preferredQuoteChar;

    private final static XmlNumberConverter NUMBER_CONVERTER = new XmlNumberConverter();

    public CssToken(int ttype, @NonNull String stringValue) {
        this(ttype, stringValue, null, 0, 0, stringValue.length());

    }

    public CssToken(int ttype, @NonNull String stringValue, @Nullable Character preferredQuoteChar) {
        this(ttype, stringValue, null, preferredQuoteChar, 0, 0, stringValue.length());

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

    public CssToken(int ttype, @Nullable String stringValue, @Nullable Number numericValue, @Nullable Character preferredQuoteChar, int lineNumber, int startPos, int endPos) {
        switch (ttype) {
            case CssTokenType.TT_DIMENSION:
                Objects.requireNonNull(numericValue, "numeric value must not be null for ttype=" + ttype);
                Objects.requireNonNull(stringValue, "string value must not be null for ttype=" + ttype);
                break;
            case CssTokenType.TT_NUMBER:
            case CssTokenType.TT_PERCENTAGE:
                Objects.requireNonNull(numericValue, "numeric value must not be null for ttype=" + ttype);
                break;
            case CssTokenType.TT_IDENT:
                if (stringValue == null || stringValue.isEmpty()) {
                    throw new IllegalArgumentException("string value must not be null or empty for ttype=" + ttype);
                }
                break;
            default:
                if (ttype < 0 && ttype != CssTokenType.TT_EOF && stringValue == null)
                    throw new IllegalArgumentException("string value must not be null for ttype=" + ttype);
                break;
        }
        this.ttype = ttype;
        this.stringValue = stringValue;
        this.numericValue = numericValue;
        this.lineNumber = lineNumber;
        this.startPos = startPos;
        this.endPos = endPos;
        this.preferredQuoteChar = preferredQuoteChar;
    }

    @Nullable
    public String getStringValueNonNull() {
        return Objects.requireNonNull(stringValue);
    }

    @Nullable
    @Override
    public String toString() {
        return fromToken();
    }

    @Nullable
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
            return fromBAD_STRING(stringValue);
        case CssTokenType.TT_BAD_URI:
            return fromBAD_URI(stringValue);
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

    @NonNull
    private String fromCDC() {
        return "<!--";
    }

    @NonNull
    private String fromCDO() {
        return "-->";
    }

    @NonNull
    private String fromIDENT() {
        return fromIDENT(stringValue);
    }

    @NonNull
    private String fromIDENT(@NonNull String value) {
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
                throw new IllegalArgumentException("nmstart missing! value=\"" + value + "\".");
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

    @NonNull
    private String fromHASHorAT(char hashOrAt, @NonNull String value) {
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

    @NonNull
    private String fromSTRING() {
        return fromSTRING(stringValue);
    }

    @NonNull
    private String fromSTRING(@NonNull String value) {
        char quoteChar =
                preferredQuoteChar != null
                        ? preferredQuoteChar
                        : value.indexOf('"') == -1 || value.indexOf('\'') == -1 ? '"' : '\'';
        return fromSTRING(value, quoteChar, quoteChar);
    }

    @NonNull
    private String fromBAD_URI(String value) {
        return fromURL(value);
    }

    @NonNull
    private String fromBAD_STRING(@NonNull String value) {
        char quoteChar =
                preferredQuoteChar != null
                        ? preferredQuoteChar
                        : value.indexOf('"') == -1 || value.indexOf('\'') == -1 ? '"' : '\'';
        return fromSTRING(value, quoteChar, '\n');
    }

    @NonNull
    private String fromSTRING(@NonNull String value, final char firstQuoteChar, final char lastQuoteChar) {
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

    @NonNull
    private String fromPERCENTAGE() {
        return Double.isFinite(numericValue.doubleValue()) ? fromNUMBER() + "%" : fromNUMBER();
    }

    @NonNull
    private String fromDIMENSION() {
        return !stringValue.isEmpty() && Double.isFinite(numericValue.doubleValue()) ? fromNUMBER() + fromIDENT() : fromNUMBER();
    }

    @NonNull
    private String fromURL() {
        return fromURL(stringValue);
    }

    @NonNull
    private String fromURL(@NonNull String stringValue) {
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

    @Nullable
    private String fromUNICODE_RANGE() {
        return stringValue;
    }

    @Nullable
    private String fromS() {
        return stringValue;
    }

    @NonNull
    private String fromCOMMENT() {
        return "/" + "*" + stringValue.replace("*" + '/', "* /") + '*' + '/';
    }

    @Nullable
    private String fromINCLUDE_MATCH() {
        return stringValue;
    }

    @Nullable
    private String fromDASH_MATCH() {
        return stringValue;
    }

    @Nullable
    private String fromPREFIX_MATCH() {
        return stringValue;
    }

    @Nullable
    private String fromSUFFIX_MATCH() {
        return stringValue;
    }

    @Nullable
    private String fromSUBSTRING_MATCH() {
        return stringValue;
    }

    @Nullable
    private String fromCOLUMN() {
        return stringValue;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    @Nullable
    public String getStringValue() {
        return stringValue;
    }

    @Nullable
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
