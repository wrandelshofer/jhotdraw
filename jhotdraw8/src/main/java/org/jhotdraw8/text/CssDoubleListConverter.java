/* @(#)CssDoubleListConverter.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.UnitConverter;

/**
 * CssDoubleListConverter.
 * <p>
 Parses a list ofCollection sizes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDoubleListConverter implements Converter<ImmutableList<Double>> {

    private final PatternConverter formatter = new PatternConverter("{0,choice,0#none|1#{1,list,{2,number}|[ ]+}}", new CssConverterFactory());
    @Nonnull
    private UnitConverter unitConverter = DefaultUnitConverter.getInstance();


    @Override
    public ImmutableList<Double> fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        ArrayList<Double> l = new ArrayList<>();
        CssTokenizerAPI tt = new CssTokenizer(buf);
        tt.setSkipWhitespaces(true);
        if (tt.nextToken() == CssTokenType.TT_IDENT && "none".equals(tt.currentStringValue())) {
            tt.skipWhitespace();
            return  ImmutableList.ofCollection(l);
        } else {
            tt.pushBack();
        }

        Loop:
        while (true) {
            switch (tt.nextToken()) {
                case CssTokenType.TT_DIMENSION: {
                    double value = tt.currentNumericValue().doubleValue();
                    l.add(unitConverter.convert(value, tt.currentStringValue(), "px"));
                    break;
                }
                case CssTokenType.TT_PERCENTAGE: {
                    double value = tt.currentNumericValue().doubleValue() / 100.0;
                    l.add(unitConverter.convert(value, "%", "px"));
                    break;
                }
                case CssTokenType.TT_NUMBER: {
                    double value = tt.currentNumericValue().doubleValue();
                    l.add(value);
                    break;
                }
                case CssTokenType.TT_IDENT: {
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
        return  ImmutableList.ofCollection(l);
    }

    @Override
    public ImmutableList<Double> getDefaultValue() {
        return ImmutableList.emptyList();
    }
    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, ImmutableList<Double> value) throws IOException {
      toStringFromCollection(out, idFactory, value);
    }
    public void toStringFromCollection(@Nonnull Appendable out, IdFactory idFactory, @Nullable Collection<Double> value) throws IOException {
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

    @Nonnull
    public String toStringFromCollection(Collection<Double> value) {
        StringBuilder out = new StringBuilder();
        try {
            toStringFromCollection(out, value);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
        return out.toString();
    }

    public void toStringFromCollection(@Nonnull Appendable out, Collection<Double> value) throws IOException {
        toStringFromCollection(out, null, value);
    }
}
