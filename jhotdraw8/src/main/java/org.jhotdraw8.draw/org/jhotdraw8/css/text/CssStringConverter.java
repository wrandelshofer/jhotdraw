/* @(#)CssStringConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts an {@code String} to a quoted CSS {@code String}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssStringConverter extends AbstractCssConverter<String> {
    private final String helpText;
    private final char quoteChar;
    @Nonnull
    private final String defaultValue;

    public CssStringConverter() {
        this(false, '\"', null);
    }

    public CssStringConverter(boolean nullable) {
        this(nullable, '\"', null);
    }

    public CssStringConverter(boolean nullable, char quoteChar, String helpText) {
        super(nullable);
        this.quoteChar = quoteChar;
        this.helpText = helpText;
        defaultValue = "" + quoteChar + quoteChar;
    }


    @Override
    public String getHelpText() {
        return helpText;
    }

    @Nonnull
    @Override
    public String parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_STRING) {
            throw new ParseException("Css String expected. " + new CssToken(tt.current(), tt.currentString()), tt.getStartPosition());
        }
        return tt.currentStringNonnull();
    }

    @Override
    protected <TT extends String> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_STRING, value, quoteChar));
    }

    @Nonnull
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }


}
