/*
 * @(#)CssPaintableConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssLinearGradient;
import org.jhotdraw8.css.CssRadialGradient;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.AbstractCssConverter;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssLinearGradientConverter;
import org.jhotdraw8.css.text.CssRadialGradientConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * CssPaintableConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paintable := (Color|LinearGradient|RadialGradient|ImagePattern|RepeatingImagePattern) ;
 * </pre>
 * <p>
 * FIXME must parse the SVG paint production and not the one from JavaFX
 * </p>
 * <a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/painting.html#SpecifyingPaint">link</a>
 *
 * @author Werner Randelshofer
 */
public class SvgPaintableConverter extends AbstractCssConverter<Paintable> {

    @NonNull
    private static final CssColorConverter colorConverter = new CssColorConverter(false);
    @NonNull
    private static final CssLinearGradientConverter linearGradientConverter = new CssLinearGradientConverter(false);
    @NonNull
    private static final CssRadialGradientConverter radialGradientConverter = new CssRadialGradientConverter(false);

    public SvgPaintableConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    protected <TT extends Paintable> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if (value instanceof CssColor) {
            CssColor c = (CssColor) value;
            colorConverter.produceTokens(c, idSupplier, out);
        } else if (value instanceof CssLinearGradient) {
            CssLinearGradient lg = (CssLinearGradient) value;
            linearGradientConverter.produceTokens(lg, idSupplier, out);
        } else if (value instanceof CssRadialGradient) {
            CssRadialGradient lg = (CssRadialGradient) value;
            radialGradientConverter.produceTokens(lg, idSupplier, out);
        } else {
            throw new UnsupportedOperationException("not yet implemented for " + value);
        }
    }

    @NonNull
    @Override
    public Paintable parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_FUNCTION) {
            switch (tt.currentStringNonNull()) {
            case CssLinearGradientConverter.LINEAR_GRADIENT_FUNCTION:
                tt.pushBack();
                return linearGradientConverter.parseNonNull(tt, idResolver);
            case CssRadialGradientConverter.RADIAL_GRADIENT_FUNCTION:
                tt.pushBack();
                return radialGradientConverter.parseNonNull(tt, idResolver);
            default:
                break;
            }
        }
        tt.pushBack();
        return colorConverter.parseNonNull(tt, idResolver);
    }

    @NonNull
    @Override
    public String getHelpText() {
        String[] lines = ("Format of ⟨Paint⟩: none｜（⟨Color⟩｜ ⟨LinearGradient⟩｜ ⟨RadialGradient⟩"
                + "\n" + colorConverter.getHelpText()
                + "\n" + linearGradientConverter.getHelpText()
                + "\n" + radialGradientConverter.getHelpText()).split("\n");
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
