/* @(#)CssColorConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Color;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.io.IdFactory;
import static org.jhotdraw8.geom.Geom.clamp;
/**
 * CssColorConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Color := (NamedColor|LookedUpColor|RgbColor|HsbColor|ColorFunction)
 * NamedColor := Word
 * LookedUpColor := Word
 * RgbColor := ("#",Digit,Digit,Digit
 *             | "#",Digit,Digit,Digit,Digit,Digit,Digit
 *             | "rgb(", Integer, ",", Integer, ",", Integer, ")"
 *             | "rgb(" Integer, "%", ",", Integer,"%","," Integer,"%" ")"
 *             | "rgba(", Integer, ",", Integer, "," Integer, ",", Double )
 *             | "rgba(", Integer "%" "," Integer, "%" "," Integer "%" "," Double )
 *  ...TODO...
 * </pre>
 * <p>
 * FIXME currently only parses the Color production
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CssColorConverter implements Converter<CssColor> {

    private static final XmlNumberConverter numberConverter = new XmlNumberConverter();

    @Override
    public void toString(Appendable out, IdFactory idFactory, CssColor value) throws IOException {
        if (value == null) {
            out.append("none");
        } else {
            out.append(value.getName());
        }
    }

    @Override
    public CssColor fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString().trim();
        CssColor c;
        if ("none".equals(str)) {
            c = null;
            buf.position(buf.limit());
        } else {
            CssTokenizerInterface tt = new CssTokenizer(new StringReader(buf.toString()));
            tt.setSkipWhitespaces(true);
            c = parseColor(tt);
            tt.skipWhitespace();
            buf.position(buf.position() + tt.getPosition());
        }
        return c;
    }

    /**
     * Parses a CSS color.
     * <pre>
     * CssColor ::= NamedColor | HexColor | ColorFunction  ;
     * NamedColor ::= TT_IDENT;
     * HexColor ::= '#' , ( hexdigit{3} | hexdigit{6} (;
     * ColorFunction ::= RGBFunction | RGBAFunction | HSBFunction | HSBAFunction ;
     * RGBFunction ::= "rgb(" , (number, number, number | percentage, percentage, percentage ), ")";
     * RGBAFunction ::= "rgba(" ,(number, number, number | percentage, percentage, percentage ),number ")";
     * HSBFunction ::= "hsb(" , number,  percentage, percentage, ")";
     * HSBAFunction ::= "hsba(" , number,  percentage, percentage, number ")";
     * <pre>
     *
     * @param tt
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public CssColor parseColor(CssTokenizerInterface tt) throws ParseException, IOException {
        CssColor color = null;
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_HASH:
                color = new CssColor('#' + tt.currentStringValue(), Color.web('#' + tt.currentStringValue()));
                break;
            case CssTokenizerInterface.TT_IDENT:
                try {
                color = new CssColor(tt.currentStringValue(), Color.web(tt.currentStringValue()));
                } catch (IllegalArgumentException e) {
                    throw new ParseException(e.getMessage()+" value:"+tt.currentStringValue(),tt.getPosition());
                }
                break;
            case CssTokenizerInterface.TT_FUNCTION:
                StringBuilder buf = new StringBuilder(tt.currentStringValue());
                buf.append('(');
                double[] values = new double[4];
                int i = 0;
                switch (tt.currentStringValue()) {
                    case "rgb":
                        while (i < 3 && (tt.nextToken() == CssTokenizerInterface.TT_NUMBER || tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE)) {
                            buf.append(numberConverter.toString(tt.currentNumericValue()));
                            if (tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE) {
                                buf.append('%');
                            }
                            values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() / 255.0 : tt.currentNumericValue().doubleValue() / 100.0;
                            if (i < 3) {
                                if (tt.nextToken() != ',') {
                                    throw new ParseException("CssColor rgb comma expected but found " + tt.currentStringValue(), tt.getPosition());
                                } else {
                                    buf.append(tt.currentStringValue());
                                }
                            }
                        }
                        if (i == 3) {
                            buf.append(')');
                            color = new CssColor(buf.toString(), new Color(clamp(values[0],0,1), clamp(values[1],0,1), clamp(values[2],0,1), 1.0));
                        } else {
                            throw new ParseException("CssColor rgb values expected but found " + tt.currentStringValue(), tt.getPosition());
                        }
                        break;
                    case "rgba":
                        while (i < 4 && (tt.nextToken() == CssTokenizerInterface.TT_NUMBER || tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE)) {
                            buf.append(numberConverter.toString(tt.currentNumericValue()));
                            if (tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE) {
                                buf.append('%');
                            }
                            if (i < 3) {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() / 255.0 : tt.currentNumericValue().doubleValue() / 100.0;
                            } else {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() : tt.currentNumericValue().doubleValue() / 100.0;
                            }
                            if (i < 4) {
                                if (tt.nextToken() != ',') {
                                    throw new ParseException("CssColor rgba comma expected but found " + tt.currentStringValue(), tt.getPosition());
                                } else {
                                    buf.append(tt.currentStringValue());
                                }
                            }
                        }
                        if (i == 4) {
                            buf.append(')');
                            color = new CssColor(buf.toString(), new Color(clamp(values[0],0,1), clamp(values[1],0,1), clamp(values[2],0,1), clamp(values[3],0,1)));
                        } else {
                            throw new ParseException("CssColor rgba values expected but found " + tt.currentStringValue(), tt.getPosition());
                        }
                        break;
                    case "hsb":
                        while (i < 3 && (tt.nextToken() == CssTokenizerInterface.TT_NUMBER || tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE)) {
                            buf.append(numberConverter.toString(tt.currentNumericValue()));
                            if (tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE) {
                                buf.append('%');
                            }
                            if (i < 1) {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() : tt.currentNumericValue().doubleValue() * 3.6;
                            } else {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() : tt.currentNumericValue().doubleValue() / 100.0;
                            }
                            if (i < 3) {
                                if (tt.nextToken() != ',') {
                                    throw new ParseException("CssColor hsb comma expected but found " + tt.currentStringValue(), tt.getPosition());
                                } else {
                                    buf.append(tt.currentStringValue());
                                }
                            }
                        }
                        if (i == 3) {
                            buf.append(')');
                            color = new CssColor(buf.toString(), Color.hsb(values[0], clamp(values[1],0,1), clamp(values[2],0,1)));
                        } else {
                            throw new ParseException("CssColor hsb values expected but found " + tt.currentStringValue(), tt.getPosition());
                        }
                        break;
                    case "hsba":
                        while (i < 4 && (tt.nextToken() == CssTokenizerInterface.TT_NUMBER || tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE)) {
                            buf.append(numberConverter.toString(tt.currentNumericValue()));
                            if (tt.currentToken() == CssTokenizerInterface.TT_PERCENTAGE) {
                                buf.append('%');
                            }
                            if (i < 1) {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() : tt.currentNumericValue().doubleValue() * 3.6;
                            } else if (i < 3) {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() : tt.currentNumericValue().doubleValue() / 100.0;
                            } else {
                                values[i++] = tt.currentToken() == CssTokenizerInterface.TT_NUMBER ? tt.currentNumericValue().doubleValue() : tt.currentNumericValue().doubleValue() / 100.0;
                            }
                            if (i < 4) {
                                if (tt.nextToken() != ',') {
                                    throw new ParseException("CssColor hsba comma expected but found " + tt.currentStringValue(), tt.getPosition());
                                } else {
                                    buf.append(tt.currentStringValue());
                                }
                            }
                        }
                        if (i == 4) {
                            buf.append(')');
                            color = new CssColor(buf.toString(), Color.hsb(values[0], clamp(values[1],0,1), clamp(values[2],0,1), clamp(values[3],0,1)));

                        } else {
                            throw new ParseException("CssColor hsba values expected but found " + tt.currentStringValue(), tt.getPosition());
                        }
                        break;
                    default:
                        throw new ParseException("CssColor expected but found " + tt.currentStringValue(), tt.getPosition());
                }
                if (tt.nextToken() != ')') {
                    throw new ParseException("CssColor ')' expected but found " + tt.currentStringValue(), tt.getPosition());
                }
                break;
            default:
                throw new ParseException("CssColor expected but found " + tt.currentStringValue(), tt.getPosition());
        }
        return color;
    }

    @Override
    public CssColor getDefaultValue() {
        return null;
    }
}
