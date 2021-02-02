/*
 * @(#)SvgPaintConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.text.CssPaintConverter;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
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
    protected <TT extends Paint> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) throws IOException {
        if ((value instanceof Color) && !value.isOpaque()) {
            Color c = (Color) value;
            Color opaqueColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 1.0);
            super.produceTokensNonNull(opaqueColor, idSupplier, out);
        } else {
            super.produceTokensNonNull(value, idSupplier, out);
        }
    }
}
