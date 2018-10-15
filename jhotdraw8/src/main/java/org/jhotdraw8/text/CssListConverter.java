package org.jhotdraw8.text;

import org.jetbrains.annotations.NotNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nullable;
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
    public ImmutableList<T> parse(@NotNull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        ArrayList<T> list = new ArrayList<>();
        do {
            T elem = elementConverter.parse(tt,idFactory);
            if (elem != null)
                list.add(elem);
            tt.setSkipWhitespaces(true);
            tt.setSkipComments(true);
        } while (tt.nextToken() == ',');
        tt.pushBack();
        return ImmutableList.ofCollection(list);
    }

    @Override
    public void produceTokens(ImmutableList<T> value, @Nullable IdFactory idFactory, @NotNull Consumer<Token> consumer) {
        if (value.isEmpty()) {
            consumer.accept(new Token(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            boolean first = true;
            for (T elem : value) {
                if (elem == null) continue;
                if (first) first = false;
                else {
                    consumer.accept(new Token(','));
                    consumer.accept(new Token(CssTokenType.TT_S, " "));
                }
                elementConverter.produceTokens(elem, idFactory, consumer);
            }
        }
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨List⟩: none | ⟨Item⟩, ⟨Item⟩, ...\n"
        +"With ⟨Item⟩:\n  "+elementConverter.getHelpText();
    }
}
