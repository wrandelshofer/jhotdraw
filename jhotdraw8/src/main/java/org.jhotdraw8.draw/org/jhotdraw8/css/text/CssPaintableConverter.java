/*
 * @(#)CssPaintableConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssLinearGradient;
import org.jhotdraw8.css.CssRadialGradient;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
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
 * FIXME currently only parses the Color and the LinearGradient productions
 * </p>
 *
 * @author Werner Randelshofer
 */
public class CssPaintableConverter extends AbstractCssConverter<Paintable> {

    private static final @NonNull CssColorConverter colorConverter = new CssColorConverter(false);
    private static final @NonNull CssLinearGradientConverter linearGradientConverter = new CssLinearGradientConverter(false);
    private static final @NonNull CssRadialGradientConverter radialGradientConverter = new CssRadialGradientConverter(false);

    public CssPaintableConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    public @NonNull String getHelpText() {
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

    @Override
    public @NonNull Paintable parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
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
}
