/* @(#)CssSizeListConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * CssSizeListConverter.
 * <p>
 * Parses a list of sizes.
 *
 * @author Werner Randelshofer
 */
public class CssSizeListConverter implements Converter<List<Double>> {

    private final PatternConverter formatter = new PatternConverter("{0,choice,0#none|1#{1,list,{2,size}|[ ]+}}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, List<Double> value) throws IOException {
        Object[] v = new Object[value.size() + 2];
        v[0] = value.size();
        v[1] = value.size();
        for (int i = 0, n = value.size(); i < n; i++) {
            v[i + 2] = value.get(i);
        }
        formatter.toString(out, v);
    }

    @Override
    public List<Double> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        ArrayList<Double> l = new ArrayList<>();
        CssTokenizerInterface tt = new CssTokenizer(buf);
        tt.setSkipWhitespaces(true);
        Loop:  while (true) {
            switch (tt.nextToken()) {
                case CssTokenizerInterface.TT_DIMENSION: {
                    double value = tt.currentNumericValue().doubleValue();
                    l.add(value);
                    break;
                }
                case CssTokenizerInterface.TT_PERCENTAGE: {
                    double value = tt.currentNumericValue().doubleValue() / 100.0;
                    l.add(value);
                    break;
                }
                case CssTokenizerInterface.TT_NUMBER: {
                    double value = tt.currentNumericValue().doubleValue();
                    l.add(value);
                    break;
                }
                default:
                    break Loop;
            }
        }
        tt.skipWhitespace();
        return l;
    }

    @Override
    public List<Double> getDefaultValue() {
        return Collections.emptyList();
    }
}
