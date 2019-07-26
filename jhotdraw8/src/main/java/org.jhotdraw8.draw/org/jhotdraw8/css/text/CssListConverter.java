/*
 * @(#)CssListConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Parses a list with items separated by commas or whitespace.
 * <p>
 * Stops parsing at EOF, semicolon and closing bracket.
 *
 * @param <T> the element type
 */
public class CssListConverter<T> implements CssConverter<ImmutableList<T>> {
    /**
     * When nonnull this comparator is used to sort the list.
     */
    @Nullable
    private final Comparator<T> comparator;

    private final CssConverter<T> elementConverter;
    private final ImmutableList<CssToken> delimiter;
    private final ImmutableList<CssToken> prefix;
    private final ImmutableList<CssToken> suffix;
    private final Set<Integer> delimiterChars;

    public CssListConverter(CssConverter<T> elementConverter) {
        this(elementConverter, ", ");
    }

    public CssListConverter(CssConverter<T> elementConverter, String delimiter) {
        this(elementConverter, delimiter, "", "");
    }

    public CssListConverter(CssConverter<T> elementConverter, String delimiter, String prefix, String suffix) {
        this(elementConverter, parseDelim(delimiter), parseDelim(prefix), parseDelim(suffix));
    }

    public CssListConverter(CssConverter<T> elementConverter, String delimiter, String prefix, String suffix, Comparator<T> comparator) {
        this(elementConverter, parseDelim(delimiter), parseDelim(prefix), parseDelim(suffix), comparator);
    }

    private static List<CssToken> parseDelim(String delim) {
        try {
            return new StreamCssTokenizer(delim).toTokenList();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public CssListConverter(CssConverter<T> elementConverter,
                            Iterable<CssToken> delimiter,
                            Iterable<CssToken> prefix,
                            Iterable<CssToken> suffix
    ) {
        this(elementConverter, delimiter, prefix, suffix, null);
    }

    public CssListConverter(CssConverter<T> elementConverter,
                            Iterable<CssToken> delimiter,
                            Iterable<CssToken> prefix,
                            Iterable<CssToken> suffix,
                            @Nullable Comparator<T> comparator
    ) {
        this.elementConverter = elementConverter;
        this.delimiter = ImmutableLists.ofIterable(delimiter);
        this.prefix = ImmutableLists.ofIterable(prefix);
        this.suffix = ImmutableLists.ofIterable(suffix);
        delimiterChars = new HashSet<>();
        for (CssToken cssToken : delimiter) {
            if (cssToken.getType() >= 0) {
                delimiterChars.add(cssToken.getType());
            }
        }
        this.comparator = comparator;
    }


    @Override
    public ImmutableList<T> parse(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT && CssTokenType.IDENT_NONE.equals(tt.currentString())) {
            return ImmutableLists.emptyList();
        } else {
            tt.pushBack();
        }

        ArrayList<T> list = new ArrayList<>();
        Loop:
        for (; ; ) {
            int ttype = tt.nextNoSkip();
            if (delimiterChars.contains(ttype)) {
                continue Loop;
            }
            switch (ttype) {
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
        if (comparator != null) {
            list.sort(comparator);
        }
        return ImmutableLists.ofCollection(list);
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
            Iterable<T> ordered;
            if (comparator != null) {
                ArrayList<T> ts = value.toArrayList();
                ts.sort(comparator);
                ordered = ts;
            } else {
                ordered = value;
            }
            for (T elem : ordered) {
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
        return ImmutableLists.emptyList();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨List⟩: ⟨Item⟩, ⟨Item⟩, ...\n"
                + "With ⟨Item⟩:\n  " + elementConverter.getHelpText();
    }
}
