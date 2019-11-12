package org.jhotdraw8.css.functions;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCssFunction<T> implements CssFunction<T> {
    private final String name;

    public AbstractCssFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    protected String evalString(@NonNull T element, @NonNull CssTokenizer tt, String expressionName, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        StringBuilder buf = new StringBuilder();
        List<CssToken> temp = new ArrayList<>();
        temp.clear();
        functionProcessor.processToken(element, tt, temp::add);
        for (CssToken t : temp) {
            switch (t.getType()) {
                case CssTokenType.TT_STRING:
                case CssTokenType.TT_URL:
                    buf.append(t.getStringValue());
                    break;
                case CssTokenType.TT_NUMBER:
                case CssTokenType.TT_DIMENSION:
                case CssTokenType.TT_PERCENTAGE:
                    buf.append(t.fromToken());
                    break;
                default:
                    throw new ParseException("〈" + expressionName + "〉: String, Number, CssSize, Percentage or URL expected.", t.getStartPos());
            }
        }
        return buf.toString();
    }

}
