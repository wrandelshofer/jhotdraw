/*
 * @(#)SvgStrokeAlignmentConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts SVG 2 stroke-alignment.
 * <pre>
 *  StrokeAlignment = "type(" , ("inner"|"outer"|"center"), ")";
 * </pre>
 * <p>
 * References:
 * <dl>
 * <dt>SVG Strokes</dt><dd><a href="https://www.w3.org/TR/svg-strokes/#SpecifyingStrokeAlignment">
 *     § 2.2. Specifying stroke alignment: the ‘stroke-alignment’ property</a></dd>
 * </dl>
 */
public class SvgStrokeAlignmentConverter extends AbstractCssConverter<StrokeType> {

    public static final String INSIDE = "inner";
    public static final String OUTSIDE = "outer";
    public static final String CENTERED = "center";

    public SvgStrokeAlignmentConverter(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public StrokeType parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        StrokeType type;
        tt.requireNextToken(CssTokenType.TT_IDENT, "One of " + INSIDE + ", " + OUTSIDE + ", " + CENTERED + " expected.");
        switch (tt.currentStringNonNull()) {
        case INSIDE:
            type = StrokeType.INSIDE;
            break;
        case OUTSIDE:
            type = StrokeType.OUTSIDE;
            break;
        case CENTERED:
            type = StrokeType.CENTERED;
            break;
        default:
            throw tt.createParseException("One of " + INSIDE + ", " + OUTSIDE + ", " + CENTERED + " expected.");
        }
        return type;
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨StrokeAlignment⟩: (inside｜outside｜centered)"
                ;
    }


    @Override
    protected <TT extends StrokeType> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        final StrokeType type = value;
        switch (type) {
        case INSIDE:
            out.accept(new CssToken(CssTokenType.TT_IDENT, INSIDE));
            break;
        case OUTSIDE:
            out.accept(new CssToken(CssTokenType.TT_IDENT, OUTSIDE));
            break;
        case CENTERED:
            out.accept(new CssToken(CssTokenType.TT_IDENT, CENTERED));
            break;
        }
    }

}
