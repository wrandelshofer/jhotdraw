package org.jhotdraw8.css.text;

import org.jetbrains.annotations.NotNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CssListConverter<T> implements CssConverter<ImmutableList<T>> {
    private final CssConverter<T> elementConverter;
    private final boolean withComma;

    public CssListConverter(CssConverter<T> elementConverter) {
        this(elementConverter, true);
    }

    public CssListConverter(CssConverter<T> elementConverter, boolean withComma) {
        this.elementConverter = elementConverter;
        this.withComma = withComma;
    }


    @Override
    public ImmutableList<T> parse(@NotNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT && CssTokenType.IDENT_NONE.equals(tt.currentString())) {
            return ImmutableList.emptyList();
        } else {
            tt.pushBack();
        }


        ArrayList<T> list = new ArrayList<>();
        do {
            T elem = elementConverter.parse(tt, idFactory);
            if (elem != null) {
                list.add(elem);
            }
        } while (tt.nextNoSkip() == CssTokenType.TT_COMMA || tt.current() == CssTokenType.TT_S);
        tt.pushBack();
        return ImmutableList.ofCollection(list);
    }

    @Override
    public <TT extends ImmutableList<T>> void produceTokens(TT value, @Nullable IdFactory idFactory, @NotNull Consumer<CssToken> out) {
        if (value == null || value.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            boolean first = true;
            for (T elem : value) {
                if (elem == null) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    if (withComma) {
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                    }
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                }
                elementConverter.produceTokens(elem, idFactory, out);
            }
        }
    }

    @Nullable
    @Override
    public ImmutableList<T> getDefaultValue() {
        return ImmutableList.emptyList();
    }

    @Override
    public String getHelpText() {
        if (withComma) {
            return "Format of ⟨List⟩: ⟨Item⟩, ⟨Item⟩, ...\n"
                    + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
        } else {
            return "Format of ⟨List⟩: ⟨Item⟩ ⟨Item⟩, ...\n"
                    + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
        }
    }
}
