/* @(#)CssPaintConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizerAPI;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.draw.key.CssRadialGradient;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.CssLinearGradient;
import org.jhotdraw8.draw.key.Paintable;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.jhotdraw8.io.IdFactory;

/**
 * CssPaintableConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paintable := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * </pre>
 * <p>
 * FIXME currently only parses the Color and the LinearGradient productions
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPaintableConverter extends AbstractCssConverter<Paintable> {

    @Nonnull
    private static final CssColorConverter colorConverter = new CssColorConverter(false);
    @Nonnull
    private static final  CssLinearGradientConverter linearGradientConverter = new CssLinearGradientConverter(false);
    @Nonnull
    private static final  CssRadialGradientConverter radialGradientConverter = new CssRadialGradientConverter(false);

    public CssPaintableConverter(boolean nullable) {
        super(nullable);
    }
    @Override
    protected <TT extends Paintable> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out) {
         if (value instanceof CssColor) {
            CssColor c = (CssColor) value;
            colorConverter.produceTokens(c,idFactory,out);
        } else if (value instanceof CssLinearGradient) {
            CssLinearGradient lg = (CssLinearGradient) value;
            linearGradientConverter.produceTokens(lg,idFactory,out);
        } else if (value instanceof CssRadialGradient) {
            CssRadialGradient lg = (CssRadialGradient) value;
            radialGradientConverter.produceTokens(lg, idFactory, out);
        } else {
            throw new UnsupportedOperationException("not yet implemented for "+value);
        }
    }

    @Nonnull
    @Override
    public Paintable parseNonnull(@Nonnull CssTokenizerAPI tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.nextToken()==CssTokenType.TT_FUNCTION) {
            switch (tt.currentStringNonnull()) {
                case CssLinearGradientConverter.LINEAR_GRADIENT_FUNCTION:
                    tt.pushBack();
                    return linearGradientConverter.parseNonnull(tt,idFactory);
                case CssRadialGradientConverter.RADIAL_GRADIENT_FUNCTION:
                    tt.pushBack();
                    return radialGradientConverter.parseNonnull(tt,idFactory);
                default:
                    break;
            };
        }
        tt.pushBack();
        return colorConverter.parseNonnull(tt, idFactory);
    }

    @Nonnull
    @Override
    public String getHelpText() {
        String[] lines = ("Format of ⟨Paint⟩: none｜（⟨Color⟩｜ ⟨LinearGradient⟩｜ ⟨RadialGradient⟩"
                + "\n" + colorConverter.getHelpText()
                + "\n" + linearGradientConverter.getHelpText()
                + "\n" + radialGradientConverter.getHelpText()).split("\n");
        ;
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
