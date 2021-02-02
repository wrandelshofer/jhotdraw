/*
 * @(#)AbstractLocator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;

/**
 * This abstract class can be extended to implement a {@link Locator}.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractLocator implements Locator {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public AbstractLocator() {
    }

    @Override
    public Point2D locate(Figure owner, Figure dependent) {
        return locate(owner);
    }

}
