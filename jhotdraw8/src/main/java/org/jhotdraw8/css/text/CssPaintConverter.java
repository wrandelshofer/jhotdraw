/* @(#)CssPaintConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssRadialGradient;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssLinearGradient;
import org.jhotdraw8.css.Paintable;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
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
public class CssPaintConverter extends AbstractCssConverter<Paint> {

    protected static final CssPaintableConverter paintableConverter = new CssPaintableConverter(false);

    public CssPaintConverter() {
        this(false);
    }
    public CssPaintConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public Paint parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        Paintable p = paintableConverter.parseNonnull(tt, idFactory);
        if (p.getPaint() == null) {
            throw new ParseException("paint is null", 0);
        }
        return p.getPaint();
    }

    @Override
    public String getHelpText() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected <TT extends Paint> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        Paintable p;
        if (value instanceof Color) {
            p = new CssColor((Color) value);
        } else if (value instanceof LinearGradient) {
            p = new CssLinearGradient((LinearGradient) value);
        } else if (value instanceof RadialGradient) {
            p = new CssRadialGradient((RadialGradient) value);
        } else {
            throw new UnsupportedOperationException("unsupported value:" + value);
        }
        paintableConverter.produceTokensNonnull(p, idFactory, out);
    }
}
