/*
 * @(#)ConcatCssFunction.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.function;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.SelectorModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.Deque;
import java.util.function.Consumer;

/**
 * Processes the concat() function.
 * <pre>
 * concat              = "concat(", string-list, ")" ;
 * string-iist         = value ,  { [ ',' ] , value } ;
 * value               = string | number | dimension | percentage | url ;
 * </pre>
 */
public class ConcatCssFunction<T> extends AbstractStringCssFunction<T> {
    /**
     * Function name.
     */
    public static final String NAME = "concat";

    public ConcatCssFunction() {
        super(NAME);
    }

    public ConcatCssFunction(String name) {
        super(name);
    }

    @Override
    public String getHelpText() {
        return getName() + "(⟨string⟩, ...)"
                + "\n    Concatenates a list of strings.";
    }


    @Override
    public void process(@NonNull T element, @NonNull CssTokenizer tt, @NonNull SelectorModel<T> model,
                        @NonNull CssFunctionProcessor<T> functionProcessor, @NonNull Consumer<CssToken> out, Deque<CssFunction<T>> recursionStack) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈concat〉: concat() function expected.");
        if (!getName().equals(tt.currentStringNonNull())) {
            throw new ParseException("〈concat〉: concat() function expected.", tt.getStartPosition());
        }

        int line = tt.getLineNumber();
        int start = tt.getStartPosition();

        StringBuilder buf = new StringBuilder();
        boolean first = true;
        while (tt.next() != CssTokenType.TT_EOF && tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            switch (tt.current()) {
            case CssTokenType.TT_COMMA:
                if (!first) {
                    continue;
                }
                tt.pushBack();
                buf.append(evalString(element, tt, getName(), functionProcessor));
                break;
            default:
                tt.pushBack();
                buf.append(evalString(element, tt, getName(), functionProcessor));
                break;
            }
            first = false;
        }
        if (tt.current() != CssTokenType.TT_RIGHT_BRACKET) {
            throw new ParseException("〈concat〉: right bracket ')' expected.", tt.getStartPosition());
        }
        int end = tt.getEndPosition();
        out.accept(new CssToken(CssTokenType.TT_STRING, buf.toString(), null, line, start, end));
    }


}
