/*
 * @(#)CssListConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Parses a list with items separated by configurable optional delimiters.
 * <p>
 * If the delimiter consists of multiple character tokens, than the parser accepts each
 * of the character tokens and any combination of them.
 * <p>
 * The delimiters are optional when the list is parsed from a String.
 * When the list is converted into a String, then the specified delimiters are used to
 * separate the items.
 * <p>
 * Stops parsing at EOF, semicolon and closing bracket.
 * <p>
 * This parser is intentionally forgiving, so that lists can be output with nice
 * delimiters, but the user does not need to type in the delimiter.
 *
 * @param <T> the element type
 */
public class CssListConverter<T> implements CssConverter<ImmutableList<T>> {
    /**
     * When nonnull this comparator is used to sort the list.
     */
    @Nullable
    private final Comparator<T> comparatorForSorting;

    private final CssConverter<T> elementConverter;
    @NonNull
    private final ImmutableList<CssToken> delimiter;
    @NonNull
    private final ImmutableList<CssToken> prefix;
    @NonNull
    private final ImmutableList<CssToken> suffix;
    @NonNull
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

    /**
     * @param elementConverter     the convert for a single element
     * @param delimiter            a String which must be parsable into character tokens, each token and any combination of them
     *                             is accepted as an optional delimiter
     * @param prefix               the prefix of the list, for example a left bracket
     * @param suffix               the suffix of the list, for example a right bracket
     * @param comparatorForSorting if this value is non-null, then it is used to sort the list
     */
    public CssListConverter(CssConverter<T> elementConverter, String delimiter, String prefix, String suffix, Comparator<T> comparatorForSorting) {
        this(elementConverter, parseDelim(delimiter), parseDelim(prefix), parseDelim(suffix), comparatorForSorting);
    }

    private static List<CssToken> parseDelim(String delim) {
        try {
            return new StreamCssTokenizer(delim).toTokenList();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public CssListConverter(CssConverter<T> elementConverter,
                            @NonNull Iterable<CssToken> delimiter,
                            Iterable<CssToken> prefix,
                            Iterable<CssToken> suffix
    ) {
        this(elementConverter, delimiter, prefix, suffix, null);
    }

    public CssListConverter(CssConverter<T> elementConverter,
                            @NonNull Iterable<CssToken> delimiter,
                            Iterable<CssToken> prefix,
                            Iterable<CssToken> suffix,
                            @Nullable Comparator<T> comparatorForSorting
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
        this.comparatorForSorting = comparatorForSorting;
    }


    @Override
    public ImmutableList<T> parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
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
                    T elem = elementConverter.parse(tt, idResolver);
                    if (elem != null) {
                        list.add(elem);
                    }
                    break;
            }

        }
        tt.pushBack();
        if (comparatorForSorting != null) {
            list.sort(comparatorForSorting);
        }
        return ImmutableLists.ofCollection(list);
    }

    @Override
    public <TT extends ImmutableList<T>> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value == null || value.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            for (CssToken t : prefix) {
                out.accept(t);
            }
            boolean first = true;
            Iterable<T> ordered;
            if (comparatorForSorting != null) {
                ArrayList<T> ts = value.toArrayList();
                ts.sort(comparatorForSorting);
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
                elementConverter.produceTokens(elem, idSupplier, out);
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
