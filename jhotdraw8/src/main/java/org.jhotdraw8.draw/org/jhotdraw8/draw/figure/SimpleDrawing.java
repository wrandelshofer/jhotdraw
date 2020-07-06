/*
 * @(#)SimpleDrawing.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;

public class SimpleDrawing extends AbstractDrawing {
    public SimpleDrawing(double width, double height) {
        super(width, height);
    }

    public SimpleDrawing() {
    }

    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }
}
