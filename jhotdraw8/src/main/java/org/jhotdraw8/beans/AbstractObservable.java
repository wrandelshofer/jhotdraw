/* @(#)AbstractObservable.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

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
     *
     * The default implementation is empty.
     */
    public void invalidated() {
    }

}
