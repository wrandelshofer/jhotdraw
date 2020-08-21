/*
 * @(#)AbstractColorCssFunction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.function;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.ListCssTokenizer;
import org.jhotdraw8.css.text.CssColorConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractColorCssFunction<T> extends AbstractCssFunction<T> {
    protected CssColorConverter converter = new CssColorConverter();

    public AbstractColorCssFunction(String name) {
        super(name);
    }

    @Nullable
    protected CssColor parseColorValue(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        CssColor color = null;
        switch (tt.next()) {
        case CssTokenType.TT_FUNCTION:
            String name = tt.currentString();
            tt.pushBack();
            List<CssToken> list = new ArrayList<>();
            functionProcessor.processToken(element, tt, list::add, new ArrayDeque<>());
            if (list.isEmpty()) {
                throw new ParseException("〈color-value〉: function " + name + "() must return a value.", tt.getStartPosition());
            }
            color = parseResolvedColorValue(element, new ListCssTokenizer(list), functionProcessor);
            break;
        default:
            tt.pushBack();
            color = parseResolvedColorValue(element, tt, functionProcessor);
            break;
        }
        return color;
    }

    @Nullable
    protected CssColor parseResolvedColorValue(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        return converter.parse(tt, null);
    }

}
