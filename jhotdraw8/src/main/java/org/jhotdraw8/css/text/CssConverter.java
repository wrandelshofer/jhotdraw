/* @(#)CssConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.scene.paint.Paint;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.draw.key.CssLinearGradient;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a data value of type {@code T} from or to a CSS Tokenizer.
 *
 * @author Werner Randelshofer
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CssConverter<T> extends Converter<T> {
    /**
     * Parses from the given tokenizer and moves the tokenizer
     * to the next token past the value.
     *
     * @param tt tokenizer positioned on the token
     * @return the parsed value
     */
    @Nullable
    T parse(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException;

    /**
     * Parses from the given tokenizer and moves the tokenizer
     * to the next token past the value.
     *
     * @param tt tokenizer positioned on the token
     * @return the parsed value
     */
    @Nonnull
    default T parseNonnull(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        T value = parse(tt, idFactory);
        if (value == null) {
            throw new ParseException("Value expected.", tt.getStartPosition());
        }
        return value;
    }

    /**
     * Produces tokens.
     */
    <TT extends T> void produceTokens(@Nullable TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out);

    /**
     * Converts the value to String.
     *
     * @param value the value
     * @param <TT>  the type
     * @return a String
     */
    default <TT extends T> String toString(@Nullable TT value) {
        return toString(value, null);
    }

    /**
     * Converts the value to String.
     *
     * @param value     the value
     * @param idFactory the id factory
     * @param <TT>      the type
     * @return a String
     */
    default <TT extends T> String toString(@Nullable TT value, @Nullable IdFactory idFactory) {
        StringBuilder buf = new StringBuilder();
        produceTokens(value, idFactory, buf::append);
        return buf.toString();
    }

    @Override
    default <TT extends T> void toString(Appendable out, IdFactory idFactory, TT value) throws IOException {
        Consumer<Token> consumer = token -> {
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


    default T fromString(CharBuffer buf, IdFactory idFactory) throws ParseException {
        try {
            return parse(new CssTokenizer(buf), idFactory);
        } catch (IOException e) {
            throw new RuntimeException("unexpected io exception", e);
        }
    }


    /**
     * Gets a help text.
     *
     * @return a help text.
     */
    String getHelpText();

}
