/*
 * @(#)CssColorConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.NamedCssColor;
import org.jhotdraw8.css.ShsbaCssColor;
import org.jhotdraw8.css.SrgbaCssColor;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.Uint4HexSrgbaCssColor;
import org.jhotdraw8.css.Uint8HexSrgbaCssColor;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * CssColorConverter.
 * <p>
 * Parses the following EBNF:
 * </p>
 * <pre>
 * CssColor ::= NamedColor | HexColor | ColorFunction  ;
 * NamedColor ::= TT_IDENT;
 * HexColor ::= ('#'|'0x') , ( hexdigit * 3 | hexdigit * 4 | hexdigit * 6 | hexdigit * 8 );
 * ColorFunction ::= RGBFunction | RGBAFunction | HSBFunction | HSBAFunction ;
 * RGBFunction ::= "rgb(" , (number, number, number | percentage, percentage, percentage ), ")";
 * RGBAFunction ::= "rgba(" ,(number, number, number | percentage, percentage, percentage ),number ")";
 * HSBFunction ::= "hsb(" , number,  percentage, percentage, ")";
 * HSBAFunction ::= "hsba(" , number,  percentage, percentage, number ")";
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssColorConverter implements CssConverter<CssColor> {
    boolean nullable;

    public CssColorConverter() {
        this(false);
    }

    public CssColorConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public @Nullable CssColor getDefaultValue() {
        return null;
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨Color⟩: " + "⟨name⟩｜#⟨hex⟩｜rgb(⟨r⟩,⟨g⟩,⟨b⟩)｜rgba(⟨r⟩,⟨g⟩,⟨b⟩,⟨a⟩)｜hsb(⟨h⟩,⟨s⟩,⟨b⟩)｜hsba(⟨h⟩,⟨s⟩,⟨b⟩,⟨a⟩)";
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public @Nullable CssColor parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        CssColor color = null;

        if (nullable) {
            if (tt.nextIsIdentNone()) {
                return null;
            } else {
                tt.pushBack();
            }
        }

        switch (tt.next()) {
            case CssTokenType.TT_DIMENSION:
                // If the color is written with a leading "0xabcdef", then the
                // color value is is tokenized into a TT_DIMENSION. The unit
                // contains the leading 'x' and the color value 'abcdef'.
                if (tt.currentNumberNonNull().intValue() == 0 && (tt.currentNumber() instanceof Long)
                        && tt.currentStringNonNull().startsWith("x")) {
                    color = parseColorHexDigits(tt.currentStringNonNull().substring(1), tt.getStartPosition());
                } else {
                    throw tt.createParseException("CssColor: hex color expected.");
                }
                break;
            case CssTokenType.TT_HASH:
                color = parseColorHexDigits(tt.currentStringNonNull(), tt.getStartPosition());
                break;
            case CssTokenType.TT_IDENT:
                String ident = tt.currentStringNonNull();
                try {
                    color = ident.startsWith("0x")
                            ? parseColorHexDigits(ident.substring(2), tt.getStartPosition())
                            : NamedCssColor.of(ident);
                } catch (IllegalArgumentException e) {
                    throw tt.createParseException(e.getMessage() + " value:" + ident);
                }
                break;
            case CssTokenType.TT_FUNCTION:
                double[] values = new double[4];
                int i = 0;
                switch (tt.currentStringNonNull()) {
                    case "rgba":
                    case "rgb": {
                        CssSize[] sizes = new CssSize[4];
                        while (i < 4 && (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_PERCENTAGE)) {
                            if (tt.current() == CssTokenType.TT_PERCENTAGE) {
                                sizes[i++] = new CssSize(tt.currentNumberNonNull().doubleValue(), UnitConverter.PERCENTAGE);
                            } else {
                                sizes[i++] = new CssSize(tt.currentNumberNonNull().doubleValue(), UnitConverter.DEFAULT);
                            }
                            if (tt.next() != ',') {
                                tt.pushBack();
                            }
                        }

                        if (i == 0) {
                            color = SrgbaCssColor.BLACK;
                            tt.pushBack();
                        } else if (i == 3) {
                            color = new SrgbaCssColor(sizes[0], sizes[1], sizes[2], CssSize.ONE);
                            tt.pushBack();
                        } else if (i == 4) {
                            color = new SrgbaCssColor(sizes[0], sizes[1], sizes[2], sizes[3]);
                        } else {
                            throw tt.createParseException("CssColor: rgb values expected.");
                        }
                        break;
                    }
                    case "hsba":
                    case "hsb": {
                        CssSize[] sizes = new CssSize[4];
                        while (i < 4 && (tt.next() == CssTokenType.TT_NUMBER
                                || tt.current() == CssTokenType.TT_PERCENTAGE
                                || tt.current() == CssTokenType.TT_DIMENSION)) {
                            if (tt.current() == CssTokenType.TT_DIMENSION &&
                                    (i != 0 || !UnitConverter.DEGREES.equals(tt.currentStringNonNull()))) {
                                throw tt.createParseException("CssColor: hsb found unsupported dimension.");
                            }
                            if (tt.current() == CssTokenType.TT_PERCENTAGE) {
                                sizes[i++] = new CssSize(tt.currentNumberNonNull().doubleValue(), UnitConverter.PERCENTAGE);
                            } else {
                                sizes[i++] = new CssSize(tt.currentNumberNonNull().doubleValue(), UnitConverter.DEFAULT);
                            }
                            if (tt.next() != ',') {
                                tt.pushBack();
                            }
                        }

                        if (i == 0) {
                            color = ShsbaCssColor.BLACK;
                            tt.pushBack();
                        } else if (i == 3) {
                            color = new ShsbaCssColor(sizes[0], sizes[1], sizes[2], CssSize.ONE);
                            tt.pushBack();
                        } else if (i == 4) {
                            color = new ShsbaCssColor(sizes[0], sizes[1], sizes[2], sizes[3]);
                        } else {
                            throw tt.createParseException("CssColor: rgb values expected.");
                        }
                        break;
                    }
                    default:
                        throw tt.createParseException("CssColor: color expected.");
                }
                if (tt.next() != ')') {
                    throw tt.createParseException("CssColor: ')' expected.");
                }
                break;
            default:
                throw tt.createParseException("CssColor: color expected.");
        }
        return color;
    }

    private @NonNull CssColor parseColorHexDigits(@NonNull String hexdigits, int startpos) throws ParseException {
        try {
            int v = (int) Long.parseLong(hexdigits, 16);
            int r, g, b, a;
            switch (hexdigits.length()) {
                case 3:
                    r = (((v & 0xf00) >>> 4) | (v & 0xf00) >>> 8);
                    g = (((v & 0x0f0)) | (v & 0x0f0) >>> 4);
                    b = ((v & 0x00f) << 4) | (v & 0x00f);
                    a = 255;
                    return new Uint4HexSrgbaCssColor(r, g, b, a);
                case 4:
                    r = (((v & 0xf000) >>> 8) | (v & 0xf000) >>> 12);
                    g = (((v & 0x0f00) >>> 4) | (v & 0x0f00) >>> 8);
                    b = (((v & 0x00f0)) | (v & 0x00f0) >>> 4);
                    a = ((v & 0x000f) << 4) | (v & 0x000f);
                    return new Uint4HexSrgbaCssColor(r, g, b, a);
                case 6:
                    r = (v & 0xff0000) >>> 16;
                    g = (v & 0x00ff00) >>> 8;
                    b = (v & 0x0000ff);
                    a = 255;
                    return new Uint8HexSrgbaCssColor(r, g, b, a);
                case 8:
                    r = (v & 0xff000000) >>> 24;
                    g = (v & 0x00ff0000) >>> 16;
                    b = (v & 0x0000ff00) >>> 8;
                    a = (v & 0xff);
                    return new Uint8HexSrgbaCssColor(r, g, b, a);
                default:
                    throw new ParseException("<hex-digits>: expected 3, 6  or 8 digits. Found:" + hexdigits, startpos);
            }
        } catch (NumberFormatException e) {
            ParseException pe = new ParseException("<hex-digits>: expected a hex-digit. Found:" + hexdigits, startpos);
            pe.initCause(e);
            throw pe;
        }
    }

    @Override
    public <TT extends CssColor> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
            return;
        }
        StreamCssTokenizer tt = new StreamCssTokenizer(value.getName());
        try {
            while (tt.next() != CssTokenType.TT_EOF) {
                out.accept(new CssToken(tt.current(), tt.currentNumber(), tt.currentString()));
            }
        } catch (IOException e) {
            throw new AssertionError("unexpected io exception", e);
        }
    }
}
