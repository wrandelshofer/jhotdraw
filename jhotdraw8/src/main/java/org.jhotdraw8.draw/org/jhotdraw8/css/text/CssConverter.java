/*
 * @(#)CssConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Converts a data value of type {@code T} from or to a CSS Tokenizer.
 *
 * @author Werner Randelshofer
 * @author Werner Randelshofer
 */
public interface CssConverter<T> extends Converter<T> {
    /**
     * Parses from the given tokenizer and moves the tokenizer
     * to the next token past the value.
     *
     * @param tt        tokenizer positioned on the token
     * @param idFactory the id factory
     * @return the parsed value
     * @throws ParseException on parse exception
     * @throws IOException    on io exception
     */
    @Nullable
    T parse(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException;

    /**
     * Parses from the given tokenizer and moves the tokenizer
     * to the next token past the value.
     *
     * @param tt        tokenizer positioned on the token
     * @param idFactory the id factory
     * @return the parsed value
     * @throws ParseException on parse exception
     * @throws IOException    on io exception
     */
    @NonNull
    default T parseNonNull(@NonNull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        T value = parse(tt, idFactory);
        if (value == null) {
            throw new ParseException("Value expected.", tt.getStartPosition());
        }
        return value;
    }

    /**
     * Produces tokens for the specified value.
     *
     * @param <TT>      the value type
     * @param value     the value
     * @param idFactory the id factory
     * @param out       the consumer for the tokens
     */
    <TT extends T> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @NonNull Consumer<CssToken> out);

    @NonNull
    default <TT extends T> List<CssToken> toTokens(@Nullable TT value, @Nullable IdFactory idFactory) {
        List<CssToken> list = new ArrayList<>();
        produceTokens(value, idFactory, list::add);
        return list;
    }

    /**
     * Converts the value to String.
     *
     * @param value the value
     * @param <TT>  the value type
     * @return a String
     */
    @NonNull
    default <TT extends T> String toString(@Nullable TT value) {
        return toString(value, null);
    }

    /**
     * Converts the value to String.
     *
     * @param value     the value
     * @param idFactory the id factory
     * @param <TT>      the value type
     * @return a String
     */
    @NonNull
    default <TT extends T> String toString(@Nullable TT value, @Nullable IdFactory idFactory) {
        StringBuilder buf = new StringBuilder();
        produceTokens(value, idFactory, buf::append);
        return buf.toString();
    }

    @Override
    default <TT extends T> void toString(@NonNull Appendable out, IdFactory idFactory, TT value) throws IOException {
        Consumer<CssToken> consumer = token -> {
            try {
                out.append(token.fromToken());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
        try {
            produceTokens(value, idFactory, consumer);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }


    default T fromString(@NonNull CharBuffer buf, IdFactory idFactory) throws ParseException {
        try {
            int startPos = buf.position();
            StreamCssTokenizer tt = new StreamCssTokenizer(buf);
            T value = parse(tt, idFactory);
            buf.position(startPos + tt.getNextPosition());
            return value;
        } catch (IOException e) {
            throw new RuntimeException("unexpected io exception", e);
        }
    }


    /**
     * Gets a help text.
     *
     * @return a help text.
     */
    @Nullable String getHelpText();

    boolean isNullable();
}
