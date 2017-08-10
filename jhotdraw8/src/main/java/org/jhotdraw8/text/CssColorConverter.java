/* @(#)CssColorConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.draw.key.CssColor;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.paint.Color;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import static org.jhotdraw8.geom.Geom.clamp;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

/**
 * CssColorConverter.
 * <p>
 * Parses the following EBNF:
 * </p>
 * <pre>
 * CssColor ::= NamedColor | HexColor | ColorFunction  ;
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
 * @version $Id$
 */
public class CssColorConverter implements Converter<CssColor> {

  private static final XmlNumberConverter numberConverter = new XmlNumberConverter();
  private final boolean optional;

  public CssColorConverter(boolean optional) {
    this.optional = optional;
  }

  @Override
  public CssColor fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
    CssColor c;
    CssTokenizerInterface tt = new CssTokenizer(new CharBufferReader(buf));
    tt.setSkipWhitespaces(true);
    c = parseColor(tt);

    if (!buf.toString().trim().isEmpty()) {
      throw new ParseException("CssColor: End expected, found:" + buf.toString(), buf.position());
    }
    return c;
  }

  @Override
  public CssColor getDefaultValue() {
    return null;
  }

  @Override
  public String getHelpText() {
    return "Format of ⟨Color⟩: " + (optional ? "none｜" : "") + "⟨name⟩｜#⟨hex⟩｜rgb(⟨r⟩,⟨g⟩,⟨b⟩)｜rgba(⟨r⟩,⟨g⟩,⟨b⟩,⟨a⟩)｜hsb(⟨h⟩,⟨s⟩,⟨b⟩)｜hsba(⟨h⟩,⟨s⟩,⟨b⟩,⟨a⟩)";
  }

  /**
   * Parses a CSS color.
   *
   * @param tt the tokenizer
   * @return the parsed color
   * @throws ParseException if parsing fails
   * @throws IOException if IO fails
   */
  public CssColor parseColor(CssTokenizerInterface tt) throws ParseException, IOException {
    CssColor color = null;
    tt.setSkipWhitespaces(true);

    if (optional) {
      if (tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
        return null;
      } else {
        tt.pushBack();
      }
    }

    switch (tt.nextToken()) {
      case CssTokenizerInterface.TT_DIMENSION:
        if (tt.currentNumericValue().intValue() == 0 && (tt.currentNumericValue() instanceof Long)
                && tt.currentStringValue().startsWith("x")) {
          color = parseColorHexDigits(tt.currentStringValue().substring(1), tt.getStartPosition());
        } else {
          throw new ParseException("CssColor: hex color expected, found:" + "0" + tt.currentStringValue(), tt.getStartPosition());
        }
        break;
      case CssTokenizerInterface.TT_HASH:
        color = parseColorHexDigits(tt.currentStringValue(), tt.getStartPosition());
        break;
      case CssTokenizerInterface.TT_IDENT:
        String ident = tt.currentStringValue();
        try {
          color = ident.startsWith("0x") ? parseColorHexDigits(ident.substring(2), tt.getStartPosition()) : new CssColor(ident, Color.web(ident));
        } catch (IllegalArgumentException e) {
          throw new ParseException(e.getMessage() + " value:" + ident, tt.getStartPosition());
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
                  throw new ParseException("CssColor: rgb comma expected but found " + tt.currentStringValue(), tt.getStartPosition());
                } else {
                  buf.append(tt.currentStringValue());
                }
              }
            }
            if (i == 0) {
              buf.append("0,0,0)");
              color = new CssColor(buf.toString(), Color.BLACK);
              tt.pushBack();
            } else if (i == 3) {
              buf.append(')');
              color = new CssColor(buf.toString(), new Color(clamp(values[0], 0, 1), clamp(values[1], 0, 1), clamp(values[2], 0, 1), 1.0));
            } else {
              throw new ParseException("CssColor: rgb values expected but found " + tt.currentStringValue(), tt.getStartPosition());
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
                  throw new ParseException("CssColor: rgba comma expected but found " + tt.currentStringValue(), tt.getStartPosition());
                } else {
                  buf.append(tt.currentStringValue());
                }
              }
            }
            if (i == 0) {
              buf.append("0,0,0,1.0)");
              color = new CssColor(buf.toString(), Color.BLACK);
              tt.pushBack();
            } else if (i == 4) {
              buf.append(')');
              color = new CssColor(buf.toString(), new Color(clamp(values[0], 0, 1), clamp(values[1], 0, 1), clamp(values[2], 0, 1), clamp(values[3], 0, 1)));
            } else {
              throw new ParseException("CssColor: 4 rgba values expected but found " + i + " values.", tt.getStartPosition());
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
                  throw new ParseException("CssColor: hsb comma expected but found " + tt.currentStringValue(), tt.getStartPosition());
                } else {
                  buf.append(tt.currentStringValue());
                }
              }
            }
            if (i == 0) {
              buf.append("0,0%,0%)");
              color = new CssColor(buf.toString(), Color.BLACK);
              tt.pushBack();
            } else if (i == 0 || i == 3) {
              buf.append(')');
              color = new CssColor(buf.toString(), Color.hsb(values[0], clamp(values[1], 0, 1), clamp(values[2], 0, 1)));
            } else {
              throw new ParseException("CssColor: hsb values expected but found " + tt.currentStringValue(), tt.getStartPosition());
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
                  throw new ParseException("CssColor: hsba comma expected but found " + tt.currentStringValue(), tt.getStartPosition());
                } else {
                  buf.append(tt.currentStringValue());
                }
              }
            }
            if (i == 0) {
              buf.append("0,0%,0%,1.0)");
              color = new CssColor(buf.toString(), Color.BLACK);
              tt.pushBack();
            } else if (i == 4) {
              buf.append(')');
              color = new CssColor(buf.toString(), Color.hsb(values[0], clamp(values[1], 0, 1), clamp(values[2], 0, 1), clamp(values[3], 0, 1)));
            } else {
              throw new ParseException("CssColor: hsba values expected but found " + tt.currentValue(), tt.getStartPosition());
            }
            break;
          default:
            throw new ParseException("CssColor: expected but found " + tt.currentValue(), tt.getStartPosition());
        }
        if (tt.nextToken() != ')') {
          throw new ParseException("CssColor: ')' expected but found " + tt.currentValue(), tt.getStartPosition());
        }
        break;
      default:
        throw new ParseException("CssColor: expected but found " + tt.currentValue(), tt.getStartPosition());
    }
    return color;
  }

  private CssColor parseColorHexDigits(String hexdigits, int startpos) throws ParseException {
    try {
      int v = (int) Long.parseLong(hexdigits, 16);
      int r, g, b, a;
      switch (hexdigits.length()) {
        case 3:
          r = (((v & 0xf00) >>> 4) | (v & 0xf00) >>> 8);
          g = (((v & 0x0f0)) | (v & 0x0f0) >>> 4);
          b = ((v & 0x00f) << 4) | (v & 0x00f);
          a = 255;
          return new CssColor('#' + hexdigits.toLowerCase(), new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
        case 4:
          r = (((v & 0xf000) >>> 8) | (v & 0xf000) >>> 12);
          g = (((v & 0x0f00) >>> 4) | (v & 0x0f00) >>> 8);
          b = (((v & 0x00f0)) | (v & 0x00f0) >>> 4);
          a = ((v & 0x000f) << 4) | (v & 0x000f);
          return new CssColor(a == 255 ? '#' + hexdigits.substring(0, 3).toLowerCase()
                  : "rgba(" + r + "," + g + "," + b + "," + a / 255.0 + ")", new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
        case 6:
          r = (v & 0xff0000) >>> 16;
          g = (v & 0x00ff00) >>> 8;
          b = (v & 0x0000ff) >>> 0;
          a = 255;
          return new CssColor('#' + hexdigits.toLowerCase(), new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
        case 8:
          r = (v & 0xff000000) >>> 24;
          g = (v & 0x00ff0000) >>> 16;
          b = (v & 0x0000ff00) >>> 8;
          a = (v & 0xff) >>> 0;
          return new CssColor(a == 255 ? '#' + hexdigits.substring(0, 6).toLowerCase()
                  : "rgba(" + r + "," + g + "," + b + "," + a / 255.0 + ")", new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0));
        default:
          throw new ParseException("illegal hex-digits, expected 3, 6  or 8 digits.Found:" + hexdigits, startpos);
      }
    } catch (NumberFormatException e) {
      ParseException pe = new ParseException("illegal hex-digits. Found:" + hexdigits, startpos);
      pe.initCause(e);
      throw pe;
    }
  }

  @Override
  public void toString(Appendable out, IdFactory idFactory, CssColor value) throws IOException {
    if (value == null) {
      out.append("none");
    } else {
      out.append(value.getName());
    }
  }
}
