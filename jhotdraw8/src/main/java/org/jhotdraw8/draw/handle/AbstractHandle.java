/* @(#)AbstractHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javax.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Figure;

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
    public boolean isCompatible(@Nonnull Handle that) {
        return that.getClass() == this.getClass();
    }

}
