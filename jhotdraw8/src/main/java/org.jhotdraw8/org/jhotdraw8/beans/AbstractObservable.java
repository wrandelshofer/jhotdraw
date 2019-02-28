/* @(#)AbstractObservable.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.InvalidationListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AbstractObservable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AbstractObservable implements ObservableMixin {

    private CopyOnWriteArrayList<InvalidationListener> invalidationListeners;

    public CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners() {
        if (invalidationListeners == null) {
            invalidationListeners = new CopyOnWriteArrayList<>();
        }
        return invalidationListeners;
    }

    /**
     * The method {@code invalidated()} can be overridden to receive
     * invalidation notifications. This is the preferred option in
     * {@code Objects} defining the property, because it requires less memory.
     * <p>
     * The default implementation is empty.
     */
    public void invalidated() {
    }

}
