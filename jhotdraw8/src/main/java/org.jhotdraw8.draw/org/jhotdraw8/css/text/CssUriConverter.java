/*
 * @(#)CssUriConverter.java
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
import java.net.URI;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts an {@code URI} to a CSS {@code URI}.
 * <pre>
 * URI = uriFunction | none ;
 * none = "none" ;
 * uriFunction = "url(" , [ uri ] , ")" ;
 * uri =  (* css uri *) ;
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssUriConverter extends AbstractCssConverter<URI> {
    private final String helpText;

    public CssUriConverter() {
        this(false, null);
    }

    public CssUriConverter(boolean nullable) {
        this(nullable, null);
    }

    public CssUriConverter(boolean nullable, String helpText) {
        super(nullable);
        this.helpText = helpText;
    }


    @Override
    public String getHelpText() {
        return helpText;
    }

    @Nonnull
    @Override
    public URI parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_URL) {
            throw new ParseException("Css URL expected. " + new CssToken(tt.current(), tt.currentString()), tt.getStartPosition());
        }
        try {
            return URI.create(tt.currentStringNonnull());
        } catch (IllegalArgumentException e) {
            throw new ParseException("Bad URL. " + new CssToken(tt.current(), tt.currentString()), tt.getStartPosition());
        }
    }

    @Override
    protected <TT extends URI> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_URL, value.toString()));
    }

    @Nonnull
    @Override
    public URI getDefaultValue() {
        return null;
    }


}
