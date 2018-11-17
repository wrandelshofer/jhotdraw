package org.jhotdraw8.css.text;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CssListConverter<T> implements CssConverter<ImmutableList<T>> {
    public enum Separator {
        SPACE,
        COMMA,
        NEWLINE,
        NEWLINE_WITH_INITIAL_NEWLINE,
        TWO_NEWLINES_WITH_TWO_INITIAL_NEWLINES
    }
    private final CssConverter<T> elementConverter;
    private final Separator style;

    public CssListConverter(CssConverter<T> elementConverter) {
        this(elementConverter, Separator.COMMA);
    }

    public CssListConverter(CssConverter<T> elementConverter, Separator separator) {
        this.elementConverter = elementConverter;
        this.style = separator;
    }


    @Override
    public ImmutableList<T> parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
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
    public <TT extends ImmutableList<T>> void produceTokens(TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
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
                    switch (style) {
                        case SPACE:
                        case NEWLINE:
                        case COMMA:
                            break;
                        case NEWLINE_WITH_INITIAL_NEWLINE:
                            out.accept(new CssToken(CssTokenType.TT_S, "\n"));
                            break;
                        case TWO_NEWLINES_WITH_TWO_INITIAL_NEWLINES:
                            out.accept(new CssToken(CssTokenType.TT_S, "\n\n"));
                            break;
                    }
                } else {
                    switch (style) {
                        case SPACE:
                            out.accept(new CssToken(CssTokenType.TT_S, " "));
                            break;
                        case NEWLINE:
                        case NEWLINE_WITH_INITIAL_NEWLINE:
                            out.accept(new CssToken(CssTokenType.TT_S, "\n"));
                            break;
                        case TWO_NEWLINES_WITH_TWO_INITIAL_NEWLINES:
                            out.accept(new CssToken(CssTokenType.TT_S, "\n\n"));
                            break;
                        case COMMA:
                            out.accept(new CssToken(CssTokenType.TT_COMMA));
                            out.accept(new CssToken(CssTokenType.TT_S, " "));
                            break;
                    }
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
        return "Format of ⟨List⟩: ⟨Item⟩, ⟨Item⟩, ...\n"
                + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
    }
}
