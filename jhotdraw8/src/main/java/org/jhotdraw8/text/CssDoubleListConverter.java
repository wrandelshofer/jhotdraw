/* @(#)CssDoubleListConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UnitConverter;

/**
 * CssDoubleListConverter.
 * <p>
 * Parses a list of sizes.
 *
 * @author Werner Randelshofer
 */
public class CssDoubleListConverter implements Converter<ImmutableObservableList<Double>> {

    private final PatternConverter formatter = new PatternConverter("{0,choice,0#none|1#{1,list,{2,number}|[ ]+}}", new CssConverterFactory());
    private UnitConverter unitConverter = DefaultUnitConverter.getInstance();


    @Override
    public ImmutableObservableList<Double> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        ArrayList<Double> l = new ArrayList<>();
        CssTokenizerInterface tt = new CssTokenizer(buf);
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenizer.TT_IDENT && "none".equals(tt.currentStringValue())) {
            tt.skipWhitespace();
            return new ImmutableObservableList<>(l);
        } else {
            tt.pushBack();
        }

        Loop:
        while (true) {
            switch (tt.nextToken()) {
                case CssTokenizerInterface.TT_DIMENSION: {
                    double value = tt.currentNumericValue().doubleValue();
                    l.add(unitConverter.convert(value, tt.currentStringValue(), "px"));
                    break;
                }
                case CssTokenizerInterface.TT_PERCENTAGE: {
                    double value = tt.currentNumericValue().doubleValue() / 100.0;
                    l.add(unitConverter.convert(value, "%", "px"));
                    break;
                }
                case CssTokenizerInterface.TT_NUMBER: {
                    double value = tt.currentNumericValue().doubleValue();
                    l.add(value);
                    break;
                }
                case CssTokenizerInterface.TT_IDENT: {
                    double value;
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
                    l.add(value);
                    break;
                }
                default:
                    break Loop;
            }
        }
        tt.skipWhitespace();
        return new ImmutableObservableList<>(l);
    }

    @Override
    public ImmutableObservableList<Double> getDefaultValue() {
        return ImmutableObservableList.emptyList();
    }
    @Override
    public void toString(Appendable out, IdFactory idFactory, ImmutableObservableList<Double> value) throws IOException {
      toStringFromCollection(out, idFactory, value);
    }
    public void toStringFromCollection(Appendable out, IdFactory idFactory, Collection<Double> value) throws IOException {
      if (value == null) {
        out.append("none");
        return;
      }
      Object[] v = new Object[value.size() + 2];
      v[0] = value.size();
      v[1] = value.size();
      Iterator<Double> iter = value.iterator();
      for (int i = 0, n = value.size(); i < n; i++) {
        v[i + 2] = iter.next();
      }
      formatter.toString(out, v);
    }

    public String toStringFromCollection(Collection<Double> value) {
        StringBuilder out = new StringBuilder();
        try {
            toStringFromCollection(out, value);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        return out.toString();
    }

    public void toStringFromCollection(Appendable out, Collection<Double> value) throws IOException {
        toStringFromCollection(out, null, value);
    }
}
