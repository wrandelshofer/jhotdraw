/* @(#)SymmetricPoint2DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javax.annotation.Nonnull;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssSymmetricPoint2DConverterOLD;

/**
 * SymmetricPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SymmetricPoint2DStyleableMapAccessor extends Point2DStyleableMapAccessor  {

    private final static long serialVersionUID = 1L;

    public SymmetricPoint2DStyleableMapAccessor(String name, @Nonnull MapAccessor<Double> xKey, @Nonnull MapAccessor<Double> yKey) {
        super(name, xKey, yKey, new CssSymmetricPoint2DConverterOLD(false));
    }
}
