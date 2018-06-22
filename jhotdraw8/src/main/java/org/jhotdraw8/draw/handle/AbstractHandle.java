/* @(#)AbstractHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Geom;

/**
 * AbstractHandle.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractHandle implements Handle {

    // ---
    // Fields
    // ---
    protected final Figure owner;

    // ---
    // Constructors
    // ---
    public AbstractHandle(Figure owner) {
        this.owner = owner;
    }

    // ---
    // Behavior
    // ---
    @Override
    public final void dispose() {
    }

    @Override
    public Figure getOwner() {
        return owner;
    }

    /**
     * Returns true if both handles have the same class.
     */
    @Override
    public boolean isCompatible(@NonNull Handle that) {
        return that.getClass() == this.getClass();
    }

}
