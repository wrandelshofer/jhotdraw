/* @(#)CssDoubleConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;

/**
 * CssDoubleConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Size := Double, [Unit] ;
 * Unit := ("px"|"mm"|"cm"|in"|"pt"|"pc"]"em"|"ex") ;
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssSizeConverter implements Converter<CssSize> {

  private final static SimpleIdFactory defaultFactory = new SimpleIdFactory();
  private final static NumberConverter numberConverter = new NumberConverter();
  private final boolean nullable;

  public CssSizeConverter() {
    this(false);
  }

  public CssSizeConverter(boolean nullable) {
    this.nullable = nullable;
  }

  @Override
  public CssSize fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
    int start=buf.position();
    CssTokenizerInterface tt = new CssTokenizer(new CharBufferReader(buf));
    CssSize sz= parseSize(tt);
    buf.position(start+tt.getEndPosition());
    return sz;
  }

  @Override
  public CssSize getDefaultValue() {
    return new CssSize(0.0, null);
  }

  public CssSize parseSize(CssTokenizerInterface tt) throws ParseException, IOException {
    tt.skipWhitespace();
    if (nullable && tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
      //tt.skipWhitespace();
      return null;
    } else {
      tt.pushBack();
    }
    Number value = null;
    String units;
    switch (tt.nextToken()) {
      case CssTokenizerInterface.TT_DIMENSION:
        value = tt.currentNumericValue();
        units = tt.currentStringValue();
        break;
      case CssTokenizerInterface.TT_PERCENTAGE:
        value = tt.currentNumericValue();
        units = "%";
        break;
      case CssTokenizerInterface.TT_NUMBER:
        value = tt.currentNumericValue();
        units = null;
        break;
      case CssTokenizerInterface.TT_IDENT: {
        switch (tt.currentStringValue()) {
          case "INF":
            value = Double.POSITIVE_INFINITY;
            break;
          case "-INF":
            value = Double.NEGATIVE_INFINITY;
            break;
          case "NaN":
            value = Double.NaN;
            break;
          default:
            throw new ParseException("number expected:" + tt.currentStringValue(), tt.getStartPosition());
        }
        units = null;
        break;
      }
      default:
        throw new ParseException("number expected", tt.getStartPosition());
    }
    return new CssSize(value.doubleValue(), units);
  }

  @Override
  public void toString(Appendable out, IdFactory idFactory, CssSize value) throws IOException {
    if (value == null) {
      if (nullable) {
        out.append("none");
        return;
      } else {
        value = getDefaultValue();
      }
    }
    numberConverter.toString(out, idFactory, value.getValue());
    if (value.getUnits() != null) {
      out.append(value.getUnits());
    }
  }
}
