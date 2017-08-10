/* @(#)CssFontConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * CssFontConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * CssFont := [FontStyle] [FontWeight] FontSize FontFamily ;
 * FontStyle := normal|italic|oblique;
 * FontWeight := normal|bold|bolder|lighter|100|200|300|400|500|600|700|800|900;
 * FontSize := Size;
 * FontFamily := Word|Quoted;
 * </pre>
 * <p>
 * FIXME currently only parses the Color production
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CssFontConverter implements Converter<CssFont> {

    private final CssDoubleConverter doubleConverter = new CssDoubleConverter();

    @Override
    public void toString(Appendable out, IdFactory idFactory, CssFont font) throws IOException {
        /// FIXME use CssTokenizer

        double fontSize = font.getSize();
        String fontFamily = font.getFamily();
        final FontPosture posture = font.getPosture();

        if (posture != null) {
            switch (font.getPosture()) {
                case ITALIC:
                    out.append("italic ");
                    break;
                case REGULAR:
                    break;
                default:
                    throw new InternalError("Unknown fontPosture:" + font.getPosture());
            }
        }
        final FontWeight weight = font.getWeight();
        if (weight != null) {
            switch (weight) {
                case NORMAL:
                    break;
                case BOLD:
                    out.append("bold ");
                    break;
                default:
                    out.append(Integer.toString(weight.getWeight()));
                    out.append(' ');
                    break;
            }
        }
        doubleConverter.toString(out, fontSize);
        out.append(' ');
        if (fontFamily.contains(" ") || fontFamily.contains("\'") || fontFamily.contains("\"")) {
            out.append('\"');
            out.append(fontFamily.replace("\"", "\\\""));
            out.append('\"');
        } else {
            out.append(fontFamily);
        }
    }

    @Override
    public CssFont fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizerInterface tt = new CssTokenizer(new StringReader(buf.toString()));
        tt.setSkipWhitespaces(true);
        FontPosture fontPosture = FontPosture.REGULAR;
        FontWeight fontWeight = FontWeight.NORMAL;
        double fontSize = 12.0;
        String fontFamily = "System";

        // parse FontStyle
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            switch (tt.currentStringValue().toLowerCase()) {
                case "normal":
                    fontPosture = FontPosture.REGULAR;
                    break;
                case "italic":
                case "oblique":
                    fontPosture = FontPosture.ITALIC;
                    break;
                default:
                    tt.pushBack();
                    break;
            }
        } else {
            tt.pushBack();
        }

        // parse FontWeight
        boolean fontWeightConsumed = false;
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            switch (tt.currentStringValue().toLowerCase()) {
                case "normal":
                    fontWeight = FontWeight.NORMAL;
                    fontWeightConsumed = true;
                    break;
                case "bold":
                    fontWeight = FontWeight.BOLD;
                    fontWeightConsumed = true;
                    break;
                case "bolder":
                    // FIXME weight should be relative to parent font
                    fontWeight = FontWeight.BOLD;
                    fontWeightConsumed = true;
                    break;
                case "lighter":
                    // FIXME weight should be relative to parent font
                    fontWeight = FontWeight.LIGHT;
                    fontWeightConsumed = true;
                    break;
                default:
                    tt.pushBack();
                    break;
            }
        } else {
            tt.pushBack();
        }

        double fontWeightOrFontSize = 0.0;
        boolean fontWeightOrFontSizeConsumed = false;
        if (!fontWeightConsumed) {
            if (tt.nextToken() == CssTokenizer.TT_NUMBER) {
                fontWeightOrFontSize = tt.currentNumericValue().doubleValue();
                fontWeightOrFontSizeConsumed = true;
            } else {
                tt.pushBack();
            }
        }

        // parse FontSize
        if (tt.nextToken() == CssTokenizer.TT_NUMBER) {
            fontSize = tt.currentNumericValue().doubleValue();

            if (fontWeightOrFontSizeConsumed) {
                switch ((int) fontWeightOrFontSize) {
                    case 100:
                        fontWeight = FontWeight.THIN;
                        break;
                    case 200:
                        fontWeight = FontWeight.EXTRA_LIGHT;
                        break;
                    case 300:
                        fontWeight = FontWeight.LIGHT;
                        break;
                    case 400:
                        fontWeight = FontWeight.NORMAL;
                        break;
                    case 500:
                        fontWeight = FontWeight.MEDIUM;
                        break;
                    case 600:
                        fontWeight = FontWeight.SEMI_BOLD;
                        break;
                    case 700:
                        fontWeight = FontWeight.BOLD;
                        break;
                    case 800:
                        fontWeight = FontWeight.EXTRA_BOLD;
                        break;
                    case 900:
                        fontWeight = FontWeight.BLACK;
                        break;
                    default:
                        throw new ParseException("illegal font weight " + fontWeightOrFontSize, buf.position() + tt.getStartPosition());
                }
            }

        } else if (fontWeightOrFontSizeConsumed) {
            tt.pushBack();
            fontSize = fontWeightOrFontSize;
        } else {
            tt.pushBack();
        }

        if (tt.nextToken() == CssTokenizer.TT_IDENT || tt.currentToken() == CssTokenizer.TT_STRING) {
            fontFamily = tt.currentStringValue();
            while (tt.nextToken() == CssTokenizer.TT_IDENT) {
                fontFamily += " " + tt.currentStringValue();
            }
            buf.position(buf.limit());
        } else if (tt.currentToken() == CssTokenizer.TT_STRING) {
            fontFamily = tt.currentStringValue();
        } else {
            throw new ParseException("font family expected", buf.position() + tt.getStartPosition());
        }
        CssFont font = CssFont.font(fontFamily, fontWeight, fontPosture, fontSize);
        if (font == null) {
            font = CssFont.font(null, fontWeight, fontPosture, fontSize);
        }
        return font;
    }

    @Override
    public CssFont getDefaultValue() {
        return null;
    }
}
