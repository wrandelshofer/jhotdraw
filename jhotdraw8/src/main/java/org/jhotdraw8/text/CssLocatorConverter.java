/* @(#)CssLocatorConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.IdFactory;

/**
 * CssLocatorConverter.
 * <p>
 * Currently converts relative locators only.
 *
 * @author Werner Randelshofer
 */
public class CssLocatorConverter implements Converter<Locator> {

  private static final XmlNumberConverter numberConverter = new XmlNumberConverter();

  public CssLocatorConverter() {
  }

  @Override
  public Locator fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
    Locator c;
    CssTokenizerInterface tt = new CssTokenizer(new CharBufferReader(buf));
    tt.setSkipWhitespaces(true);
    c = parseLocator(tt);

    if (!buf.toString().trim().isEmpty()) {
      throw new ParseException("Locator: End expected, found:" + buf.toString(), buf.position());
    }
    return c;
  }

  @Override
  public Locator getDefaultValue() {
    return null;
  }

  @Override
  public String getHelpText() {
    return "Format of ⟨Locator⟩: relative(⟨x⟩%,⟨y⟩%)";
  }

    /**
     * Parses a Locator.
     *
     * @param tt the tokenizer
     * @return the parsed color
     * @throws ParseException if parsing fails
     * @throws IOException if IO fails
     */
    public Locator parseLocator(CssTokenizerInterface tt) throws ParseException, IOException {
        Locator color = null;
        tt.setSkipWhitespaces(true);

        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_FUNCTION:
                if (!"relative".equals(tt.currentStringValue())) {
                    throw new ParseException("Locator: function 'relative(' expected, found:" + tt.currentValue(), tt.getStartPosition());
                }
                break;
            default:
                throw new ParseException("Locator: function expected, found:" + tt.currentValue(), tt.getStartPosition());
        }
        double x, y;

        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_NUMBER:
                x = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizer.TT_PERCENTAGE:
                x = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            default:
                throw new ParseException("RelativeLocator: x-value expected but found " + tt.currentValue(), tt.getStartPosition());
        }
        switch (tt.nextToken()) {
            case ',':
                break;
            default:
                tt.pushBack();
                break;
        }
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_NUMBER:
                y = tt.currentNumericValue().doubleValue();
                break;
            case CssTokenizer.TT_PERCENTAGE:
                y = tt.currentNumericValue().doubleValue() / 100.0;
                break;
            default:
                throw new ParseException("RelativeLocator: y-value expected but found " + tt.currentValue(), tt.getStartPosition());
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("RelativeLocator: ')' expected but found " + tt.currentValue(), tt.getStartPosition());
        }

        return new RelativeLocator(x, y);
    }


  @Override
  public void toString(Appendable out, IdFactory idFactory, Locator value) throws IOException {
    if (value instanceof RelativeLocator) {
        RelativeLocator rl=(RelativeLocator)value;
      out.append("relative(");
      out.append(numberConverter.toString(rl.getRelativeX()*100));
      out.append("%,");
       out.append(numberConverter.toString(rl.getRelativeY()*100));
      out.append("%)");
    } else {
     throw new UnsupportedOperationException("only RelativeLocator supported, value:"+value);
    }
  }
}
