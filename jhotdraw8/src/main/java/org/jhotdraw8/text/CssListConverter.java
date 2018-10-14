package org.jhotdraw8.text;

import org.jetbrains.annotations.NotNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenizerInterface;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CssListConverter<T> implements CssConverter<ImmutableList<T>> {
    private final CssConverter<T> elementConverter;

    public CssListConverter(CssConverter<T> elementConverter) {
        this.elementConverter = elementConverter;
    }


    @Override
    public ImmutableList<T> parse(@NotNull CssTokenizerInterface tt) throws ParseException, IOException {
        ArrayList<T> list = new ArrayList<>();
        do {
            T elem = elementConverter.parse(tt);
            if (elem != null)
                list.add(elem);
            tt.setSkipWhitespaces(true);
            tt.setSkipComments(true);
        } while (tt.nextToken() == ',');
        tt.pushBack();
        return ImmutableList.ofCollection(list);
    }

    @Override
    public void produceTokens(ImmutableList<T> value, @NotNull Consumer<CssToken> consumer) {
        if (value.isEmpty()) {
            consumer.accept(new CssToken(CssToken.TT_IDENT, CssToken.IDENT_NONE));
        } else {
            boolean first = true;
            for (T elem : value) {
                if (elem == null) continue;
                if (first) first = false;
                else {
                    consumer.accept(new CssToken(','));
                    consumer.accept(new CssToken(CssToken.TT_S, " "));
                }
                elementConverter.produceTokens(elem, consumer);
            }
        }
    }
}
