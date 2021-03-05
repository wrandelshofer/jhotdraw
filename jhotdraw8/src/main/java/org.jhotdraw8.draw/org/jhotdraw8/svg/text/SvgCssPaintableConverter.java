/*
 * @(#)SvgCssPaintableConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * SvgCssPaintableConverter.
 *
 * @author Werner Randelshofer
 */
public class SvgCssPaintableConverter extends AbstractCssConverter<Paintable> {
    /** The currentColor keyword. */
    public static final String CURRENT_COLOR_KEYWORD = "currentColor";

    private static final @NonNull CssColorConverter colorConverter = new CssColorConverter(false);

    public SvgCssPaintableConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    protected <TT extends Paintable> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value instanceof CssColor) {
            CssColor c = (CssColor) value;
            colorConverter.produceTokens(c, idSupplier, out);
        } else {
            throw new UnsupportedOperationException("not yet implemented for " + value);
        }
    }

    @Override
    public @NonNull Paintable parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_URL) {
            String url = tt.currentStringNonNull();
            if (url.startsWith("#")) {
                Object object = idResolver.getObject(url.substring(1));
                if (object instanceof Paintable) {
                    return (Paintable) object;
                }
            }
            throw tt.createParseException("SvgPaintable illegal URL: " + url);
        } else {
            tt.pushBack();
            return colorConverter.parseNonNull(tt, idResolver);
        }
    }

    @Override
    public @NonNull String getHelpText() {
        String[] lines = ("Format of ⟨Paint⟩: none｜（⟨Color⟩｜ ⟨LinearGradient⟩｜ ⟨RadialGradient⟩"
                + "\n" + colorConverter.getHelpText()).split("\n");
        StringBuilder buf = new StringBuilder();
        Set<String> duplicateLines = new HashSet<>();
        for (String line : lines) {
            if (duplicateLines.add(line)) {
                if (buf.length() != 0) {
                    buf.append('\n');
                }
                buf.append(line);
            }
        }
        return buf.toString();
    }
}
