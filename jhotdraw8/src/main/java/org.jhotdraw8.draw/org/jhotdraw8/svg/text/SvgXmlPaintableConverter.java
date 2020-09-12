/*
 * @(#)SvgXmlPaintableConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.ResolvingConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Consumer;

public class SvgXmlPaintableConverter extends AbstractCssConverter<Paintable> implements ResolvingConverter<Paintable> {
    private final SvgCssPaintableConverter cssPaintableConverter = new SvgCssPaintableConverter(false);

    public SvgXmlPaintableConverter() {
        this(true);
    }

    public SvgXmlPaintableConverter(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public Paintable parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        Objects.requireNonNull(idResolver);
        if (tt.next() == CssTokenType.TT_URL) {
            Object object = idResolver.getObject(tt.currentStringNonNull());
            if (object instanceof Paintable) {
                return (Paintable) object;
            } else {
                throw tt.createParseException("<Paintable> expected.");
            }
        }
        tt.pushBack();
        return cssPaintableConverter.parseNonNull(tt, idResolver);
    }

    @Override
    public @Nullable String getHelpText() {
        return null;
    }

    @Override
    protected <TT extends Paintable> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        cssPaintableConverter.produceTokensNonNull(value, idSupplier, out);
    }
}
