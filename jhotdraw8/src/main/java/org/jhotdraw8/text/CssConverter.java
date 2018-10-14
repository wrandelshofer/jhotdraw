/* @(#)CssConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a data value of type {@code T} from or to a CSS Tokenizer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CssConverter<T> {
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
     * Produces tokens.
     */
    void produceTokens(@Nullable T value, @Nullable IdFactory idFactory,@Nonnull Consumer<Token> consumer);
}
