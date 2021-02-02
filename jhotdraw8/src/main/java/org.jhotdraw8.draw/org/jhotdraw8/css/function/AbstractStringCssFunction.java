/*
 * @(#)AbstractStringCssFunction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.function;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStringCssFunction<T> extends AbstractCssFunction<T> {
    public AbstractStringCssFunction(String name) {
        super(name);
    }
    private final static               ParseException parseException = new ParseException("〈---〉: String, Number, CssSize, Percentage or URL expected.", 0);


    @NonNull
    protected String evalString(@NonNull T element, @NonNull CssTokenizer tt, String expressionName, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        StringBuilder buf = new StringBuilder();
        List<CssToken> temp = new ArrayList<>();

        int count = 0;

        // skip white space
        while (tt.next() == CssTokenType.TT_S) {
        }
        tt.pushBack();

        functionProcessor.processToken(element, tt, temp::add, new ArrayDeque<>());
        for (CssToken t : temp) {
            switch (t.getType()) {
            case CssTokenType.TT_STRING:
            case CssTokenType.TT_URL:
                buf.append(t.getStringValue());
                count++;
                break;
            case CssTokenType.TT_NUMBER:
            case CssTokenType.TT_DIMENSION:
            case CssTokenType.TT_PERCENTAGE:
                buf.append(t.fromToken());
                count++;
                break;
            default:
                throw parseException;
               // throw new ParseException("〈" + expressionName + "〉: String, Number, CssSize, Percentage or URL expected.", t.getStartPos());
            }
        }
        if (count == 0) {
            throw parseException;
          //  throw new ParseException("〈" + expressionName + "〉: String, Number, CssSize, Percentage or URL expected.", 0);
        }

        return buf.toString();
    }

}
