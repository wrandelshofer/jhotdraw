/* @(#)SimpleObservable.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;

import javax.annotation.Nonnull;

/**
 * SimpleObservable.
 *
 * @design.pattern SimpleObservable Observer, ConcreteSubject.
 * {@link SimpleObservable} is a concrete subject implementation of the Observer
 * pattern.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleObservable implements ObservableMixin {

    private final CopyOnWriteArrayList<InvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();

    @Nonnull
    public CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners() {
        return invalidationListeners;
    }
}
