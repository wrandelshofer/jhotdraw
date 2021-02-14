/*
 * @(#)SvgColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.text;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.render.RenderContext;

public class SvgColor extends CssColor {
    public SvgColor(@NonNull Color color) {
        super(color);
    }

    public SvgColor(@Nullable String name) {
        super(name);
    }

    public SvgColor(@Nullable String name, @NonNull Color color) {
        super(name, color);
    }

}
