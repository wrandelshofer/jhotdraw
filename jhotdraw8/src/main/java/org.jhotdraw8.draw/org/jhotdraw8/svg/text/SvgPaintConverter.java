/*
 * @(#)SvgPaintConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.text.CssPaintConverter;
import org.jhotdraw8.io.IdFactory;

import java.util.function.Consumer;

/**
 * SvgPaintConverter.
 * <p>
 * SVG does not support an alpha channel in a color. The opacity must be
 * specified in a separate attribute.
 *
 * @author Werner Randelshofer
 */
public class SvgPaintConverter extends CssPaintConverter {

    public SvgPaintConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    protected <TT extends Paint> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if ((value instanceof Color) && !value.isOpaque()) {
            Color c = (Color) value;
            Color opaqueColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 1.0);
            super.produceTokensNonnull(opaqueColor, idFactory, out);
        } else {
            super.produceTokensNonnull(value, idFactory, out);
        }
    }
}
