package org.jhotdraw8.css.text;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.IdFactory;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Parses a list with items separated by commas or whitespace.
 *
 * @param <T>
 */
public class CssListConverter<T> implements CssConverter<ImmutableList<T>> {

    private final CssConverter<T> elementConverter;
    private final ImmutableList<CssToken> delimiter;
    private final ImmutableList<CssToken> prefix;
    private final ImmutableList<CssToken> suffix;

    public CssListConverter(CssConverter<T> elementConverter) {
        this(elementConverter, ", ");
    }

    public CssListConverter(CssConverter<T> elementConverter, String delimiter) {
        this(elementConverter, delimiter, "", "");
    }

    public CssListConverter(CssConverter<T> elementConverter, String delimiter, String prefix, String suffix) {
        this(elementConverter, parseDelim(delimiter), parseDelim(prefix), parseDelim(suffix));
    }

    private static List<CssToken> parseDelim(String delim) {
        try {
            return new StreamCssTokenizer(delim).toTokenList();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public CssListConverter(CssConverter<T> elementConverter,
                            ReadOnlyList<CssToken> delimiter,
                            ReadOnlyList<CssToken> prefix,
                            ReadOnlyList<CssToken> suffix
    ) {
        this.elementConverter = elementConverter;
        this.delimiter = ImmutableList.ofCollection(delimiter);
        this.prefix = ImmutableList.ofCollection(prefix);
        this.suffix = ImmutableList.ofCollection(suffix);
    }

    public CssListConverter(CssConverter<T> elementConverter,
                            List<? extends CssToken> delimiter,
                            List<? extends CssToken> prefix,
                            List<? extends CssToken> suffix
    ) {
        this.elementConverter = elementConverter;
        this.delimiter = ImmutableList.ofCollection(delimiter);
        this.prefix = ImmutableList.ofCollection(prefix);
        this.suffix = ImmutableList.ofCollection(suffix);
    }


    @Override
    public ImmutableList<T> parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT && CssTokenType.IDENT_NONE.equals(tt.currentString())) {
            return ImmutableList.emptyList();
        } else {
            tt.pushBack();
        }

        ArrayList<T> list = new ArrayList<>();
        Loop:
        for (; ; ) {
            switch (tt.nextNoSkip()) {
                case CssTokenType.TT_COMMA:
                case CssTokenType.TT_S:
                    continue Loop;
                case CssTokenType.TT_EOF:
                case CssTokenType.TT_SEMICOLON:
                case CssTokenType.TT_RIGHT_BRACKET:
                case CssTokenType.TT_RIGHT_CURLY_BRACKET:
                case CssTokenType.TT_RIGHT_SQUARE_BRACKET:
                    tt.pushBack();
                    break Loop;
                default:
                    tt.pushBack();
                    T elem = elementConverter.parse(tt, idFactory);
                    if (elem != null) {
                        list.add(elem);
                    }
                    break;
            }

        }
        tt.pushBack();
        return ImmutableList.ofCollection(list);
    }

    @Override
    public <TT extends ImmutableList<T>> void produceTokens(TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value == null || value.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            for (CssToken t : prefix) {
                out.accept(t);
            }
            boolean first = true;
            for (T elem : value) {
                if (elem == null) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    for (CssToken t : delimiter) {
                        out.accept(t);
                    }
                }
                elementConverter.produceTokens(elem, idFactory, out);
            }
            for (CssToken t : suffix) {
                out.accept(t);
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
