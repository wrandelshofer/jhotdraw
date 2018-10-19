/* @(#)SvgPaintConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.text;

import java.util.function.Consumer;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.css.ast.Token;
import org.jhotdraw8.css.text.CssPaintConverter;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * SvgPaintConverter.
 * <p>
 * SVG does not support an alpha channel in a color. The opacity must be
 * specified in a separate attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SvgPaintConverter extends CssPaintConverter {

    public SvgPaintConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    protected <TT extends Paint> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<Token> out) {
        if ((value instanceof Color) && !value.isOpaque()) {
            Color c = (Color) value;
            Color opaqueColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 1.0);
            super.produceTokensNonnull(opaqueColor, idFactory, out);
        } else {
            super.produceTokensNonnull(value, idFactory, out);
        }
    }
}
