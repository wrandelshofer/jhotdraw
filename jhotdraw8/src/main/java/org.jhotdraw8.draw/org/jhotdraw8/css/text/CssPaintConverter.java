/*
 * @(#)CssPaintConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssLinearGradient;
import org.jhotdraw8.css.CssRadialGradient;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

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
 */
public class CssPaintConverter extends AbstractCssConverter<Paint> {

    protected static final CssPaintableConverter paintableConverter = new CssPaintableConverter(false);

    public CssPaintConverter() {
        this(false);
    }

    public CssPaintConverter(boolean nullable) {
        super(nullable);
    }

    @NonNull
    @Override
    public Paint parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        Paintable p = paintableConverter.parseNonNull(tt, idResolver);
        if (p.getPaint() == null) {
            throw new ParseException("paint is null", 0);
        }
        return p.getPaint();
    }

    @Override
    public String getHelpText() {
        return paintableConverter.getHelpText();
    }

    @Override
    protected <TT extends Paint> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
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
        paintableConverter.produceTokensNonNull(p, idSupplier, out);
    }
}
