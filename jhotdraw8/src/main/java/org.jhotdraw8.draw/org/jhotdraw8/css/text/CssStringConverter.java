/*
 * @(#)CssStringConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts an {@code String} to a quoted CSS {@code String}.
 *
 * @author Werner Randelshofer
 */
public class CssStringConverter extends AbstractCssConverter<String> {
    private final String helpText;
    private final char quoteChar;
    @NonNull
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

    @NonNull
    @Override
    public String parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_STRING) {
            throw new ParseException("Css String expected. " + tt.getToken(), tt.getStartPosition());
        }
        return tt.currentStringNonNull();
    }

    @Override
    protected <TT extends String> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_STRING, value, quoteChar));
    }

    @NonNull
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }


}
