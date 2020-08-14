/*
 * @(#)CssStrokeStyleConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.MappedConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts SVG font-size.
 * <p>
 * Reference:
 * <dl>
 * <dt>Font Size</dt><dd><a href="https://www.w3.org/TR/css-fonts-3/#font-size-prop">
 *     link</a></dd>
 * </dl>
 */
public class SvgFontSizeConverter extends AbstractCssConverter<SvgFontSize> {

    private final MappedConverter<SvgFontSize.SizeKeyword> mappedConverter =
            new MappedConverter<SvgFontSize.SizeKeyword>(ImmutableMaps.ofEntries(
                    ImmutableMaps.entry("xx-small", SvgFontSize.SizeKeyword.XX_SMALL),
                    ImmutableMaps.entry("x-small", SvgFontSize.SizeKeyword.X_SMALL),
                    ImmutableMaps.entry("small", SvgFontSize.SizeKeyword.SMALL),
                    ImmutableMaps.entry("medium", SvgFontSize.SizeKeyword.MEDIUM),
                    ImmutableMaps.entry("large", SvgFontSize.SizeKeyword.LARGE),
                    ImmutableMaps.entry("x-large", SvgFontSize.SizeKeyword.X_LARGE),
                    ImmutableMaps.entry("xx-large", SvgFontSize.SizeKeyword.XX_LARGE),
                    ImmutableMaps.entry("smaller", SvgFontSize.SizeKeyword.SMALLER),
                    ImmutableMaps.entry("larger", SvgFontSize.SizeKeyword.LARGER)
            ).asMap());
    private final CssSizeConverter sizeConverter = new CssSizeConverter(false);

    public SvgFontSizeConverter() {
        super(false);
    }


    @NonNull
    @Override
    public SvgFontSize parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        SvgFontSize.SizeKeyword sizeKeyword;
        CssSize cssSize;
        if (tt.next() == CssTokenType.TT_IDENT) {
            sizeKeyword = mappedConverter.fromString(tt.currentStringNonNull());
            cssSize = null;
        } else {
            tt.pushBack();
            sizeKeyword = null;
            cssSize = sizeConverter.parseNonNull(tt, idResolver);
        }
        return new SvgFontSize(sizeKeyword, cssSize);
    }

    @Override
    public @Nullable String getHelpText() {
        return null;
    }

    @Override
    protected <TT extends SvgFontSize> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value.getKeyword() != null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, mappedConverter.toString(value.getKeyword())));
        } else if (value.getLength() != null) {
            sizeConverter.produceTokens(value.getLength(), idSupplier, out);
        } else {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        }
    }
}
