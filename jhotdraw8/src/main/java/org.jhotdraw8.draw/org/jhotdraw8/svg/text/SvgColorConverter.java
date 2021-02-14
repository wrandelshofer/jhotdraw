/*
 * @(#)SvgColorConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import static org.jhotdraw8.geom.Geom.clamp;

/**
 * SvgColorConverter.
 * <p>
 * Parses the following EBNF:
 * </p>
 * <pre>
 * SvgColor ::= NamedColor | HexColor | ColorFunction  ;
 * NamedColor ::= TT_IDENT;
 * HexColor ::= ('#'|'0x') , ( hexdigit{3} | hexdigit{4} | hexdigit{6} | hexdigit{8} );
 * ColorFunction ::= RGBFunction | RGBAFunction | HSBFunction | HSBAFunction ;
 * RGBFunction ::= "rgb(" , (number, number, number | percentage, percentage, percentage ), ")";
 * RGBAFunction ::= "rgba(" ,(number, number, number | percentage, percentage, percentage ),number ")";
 * HSBFunction ::= "hsb(" , number,  percentage, percentage, ")";
 * HSBAFunction ::= "hsba(" , number,  percentage, percentage, number ")";
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class SvgColorConverter implements CssConverter<SvgColor> {
    boolean nullable;

    public SvgColorConverter() {
        this(false);
    }

    public SvgColorConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public @Nullable SvgColor getDefaultValue() {
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
    public @Nullable SvgColor parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        SvgColor color = null;

        if (nullable) {
            if (tt.nextIsIdentNone()) {
                return null;
            } else {
                tt.pushBack();
            }
        }

        switch (tt.next()) {
            case CssTokenType.TT_DIMENSION:
                if (tt.currentNumberNonNull().intValue() == 0 && (tt.currentNumber() instanceof Long)
                        && tt.currentStringNonNull().startsWith("x")) {
                    color = parseColorHexDigits(tt.currentStringNonNull().substring(1), tt.getStartPosition());
                } else {
                    throw tt.createParseException("SvgColor: hex color expected.");
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
                            : new SvgColor(ident);
                } catch (IllegalArgumentException e) {
                    throw tt.createParseException(e.getMessage() + " value:" + ident);
                }
                break;
            case CssTokenType.TT_FUNCTION:
                StringBuilder buf = new StringBuilder(tt.currentStringNonNull());
                buf.append('(');
                double[] values = new double[4];
                int i = 0;
                switch (tt.currentStringNonNull()) {
                    case "rgb":
                        while (i < 3 && (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_PERCENTAGE)) {
                            buf.append(new CssToken(tt.current(), tt.currentNumber(), tt.currentString()));
                            values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() / 255.0 : tt.currentNumberNonNull().doubleValue() / 100.0;
                            if (i < 3) {
                                if (tt.next() != ',') {
                                    throw tt.createParseException("SvgColor: rgb comma expected.");
                                } else {
                                    buf.append(tt.currentString());
                                }
                            }
                        }
                        if (i == 0) {
                            buf.append("0,0,0)");
                            color = new SvgColor(buf.toString(), Color.BLACK);
                            tt.pushBack();
                        } else if (i == 3) {
                            buf.append(')');
                            color = new SvgColor(buf.toString(), new Color(clamp(values[0], 0, 1), clamp(values[1], 0, 1), clamp(values[2], 0, 1), 1.0));
                        } else {
                            throw tt.createParseException("SvgColor: rgb values expected.");
                        }
                        break;
                    case "rgba":
                        while (i < 4 && (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_PERCENTAGE)) {
                            buf.append(new CssToken(tt.current(), tt.currentNumber(), tt.currentString()));
                            if (i < 3) {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() / 255.0 : tt.currentNumberNonNull().doubleValue() / 100.0;
                            } else {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() : tt.currentNumberNonNull().doubleValue() / 100.0;
                            }
                            if (i < 4) {
                                if (tt.next() != ',') {
                                    throw tt.createParseException("SvgColor: rgba comma expected.");
                                } else {
                                    buf.append(tt.currentString());
                                }
                            }
                        }
                        if (i == 0) {
                            buf.append("0,0,0,1.0)");
                            color = new SvgColor(buf.toString(), Color.BLACK);
                            tt.pushBack();
                        } else if (i == 4) {
                            buf.append(')');
                            color = new SvgColor(buf.toString(), new Color(clamp(values[0], 0, 1), clamp(values[1], 0, 1), clamp(values[2], 0, 1), clamp(values[3], 0, 1)));
                        } else {
                            throw tt.createParseException("SvgColor: 4 rgba values expected.");
                        }
                        break;
                    case "hsb":
                        while (i < 3 && (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_PERCENTAGE)) {
                            buf.append(new CssToken(tt.current(), tt.currentNumber(), tt.currentString()));
                            if (i < 1) {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() : tt.currentNumberNonNull().doubleValue() * 3.6;
                            } else {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() : tt.currentNumberNonNull().doubleValue() / 100.0;
                            }
                            if (i < 3) {
                                if (tt.next() != ',') {
                                    throw tt.createParseException("SvgColor: hsb comma expected.");
                                } else {
                                    buf.append(tt.currentString());
                                }
                            }
                        }
                        if (i == 0) {
                            buf.append("0,0%,0%)");
                            color = new SvgColor(buf.toString(), Color.BLACK);
                            tt.pushBack();
                        } else if (i == 3) {
                            buf.append(')');
                            color = new SvgColor(buf.toString(), Color.hsb(values[0], clamp(values[1], 0, 1), clamp(values[2], 0, 1)));
                        } else {
                            throw tt.createParseException("SvgColor: hsb values expected.");
                        }
                        break;
                    case "hsba":
                        while (i < 4 && (tt.next() == CssTokenType.TT_NUMBER || tt.current() == CssTokenType.TT_PERCENTAGE)) {
                            buf.append(new CssToken(tt.current(), tt.currentNumber(), tt.currentString()));
                            if (i < 1) {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() : tt.currentNumberNonNull().doubleValue() * 3.6;
                            } else if (i < 3) {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() : tt.currentNumberNonNull().doubleValue() / 100.0;
                            } else {
                                values[i++] = tt.current() == CssTokenType.TT_NUMBER ? tt.currentNumberNonNull().doubleValue() : tt.currentNumberNonNull().doubleValue() / 100.0;
                            }
                            if (i < 4) {
                                if (tt.next() != ',') {
                                    throw tt.createParseException("SvgColor: hsba comma expected.");
                                } else {
                                    buf.append(tt.currentString());
                                }
                            }
                        }
                        if (i == 0) {
                            buf.append("0,0%,0%,1.0)");
                            color = new SvgColor(buf.toString(), Color.BLACK);
                            tt.pushBack();
                        } else if (i == 4) {
                            buf.append(')');
                            color = new SvgColor(buf.toString(), Color.hsb(values[0], clamp(values[1], 0, 1), clamp(values[2], 0, 1), clamp(values[3], 0, 1)));
                        } else {
                            throw tt.createParseException("SvgColor: hsba values expected.");
                        }
                        break;
                    default:
                        throw tt.createParseException("SvgColor: color expected.");
                }
                if (tt.next() != ')') {
                    throw tt.createParseException("SvgColor: ')' expected.");
                }
                break;
            default:
                throw tt.createParseException("SvgColor: color expected.");
        }
        return color;
    }

    private @NonNull SvgColor parseColorHexDigits(@NonNull String hexdigits, int startpos) throws ParseException {
        try {
            int v = (int) Long.parseLong(hexdigits, 16);
            int r, g, b, a;
            switch (hexdigits.length()) {
                case 3:
                    r = (((v & 0xf00) >>> 4) | (v & 0xf00) >>> 8);
                    g = (((v & 0x0f0)) | (v & 0x0f0) >>> 4);
                    b = ((v & 0x00f) << 4) | (v & 0x00f);
                    a = 255;
                    return new SvgColor('#' + hexdigits.toLowerCase(), new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
                case 4:
                    r = (((v & 0xf000) >>> 8) | (v & 0xf000) >>> 12);
                    g = (((v & 0x0f00) >>> 4) | (v & 0x0f00) >>> 8);
                    b = (((v & 0x00f0)) | (v & 0x00f0) >>> 4);
                    a = ((v & 0x000f) << 4) | (v & 0x000f);
                    return new SvgColor(a == 255 ? '#' + hexdigits.substring(0, 3).toLowerCase()
                            : "rgba(" + r + "," + g + "," + b + "," + a / 255.0 + ")", new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
                case 6:
                    r = (v & 0xff0000) >>> 16;
                    g = (v & 0x00ff00) >>> 8;
                    b = (v & 0x0000ff);
                    a = 255;
                    return new SvgColor('#' + hexdigits.toLowerCase(), new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
                case 8:
                    r = (v & 0xff000000) >>> 24;
                    g = (v & 0x00ff0000) >>> 16;
                    b = (v & 0x0000ff00) >>> 8;
                    a = (v & 0xff);
                    return new SvgColor(a == 255 ? '#' + hexdigits.substring(0, 6).toLowerCase()
                            : "rgba(" + r + "," + g + "," + b + "," + a / 255.0 + ")", new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
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
    public <TT extends SvgColor> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
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
