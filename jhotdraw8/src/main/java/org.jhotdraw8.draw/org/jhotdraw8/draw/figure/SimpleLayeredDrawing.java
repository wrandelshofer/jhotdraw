package org.jhotdraw8.draw.figure;

import org.jhotdraw8.css.CssSize;

public class SimpleLayeredDrawing extends AbstractDrawing implements LayeredDrawing {
    public SimpleLayeredDrawing() {
    }

    public SimpleLayeredDrawing(double width, double height) {
        super(width, height);
    }

    public SimpleLayeredDrawing(CssSize width, CssSize height) {
        super(width, height);
    }
}
