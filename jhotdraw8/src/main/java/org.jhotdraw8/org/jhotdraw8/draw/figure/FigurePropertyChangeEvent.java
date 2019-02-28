/* @(#)FigurePropertyChangeEvent.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.event.Event;

/**
 * FigurePropertyChangeEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigurePropertyChangeEvent extends Event<Figure> {

    private final static long serialVersionUID = 1L;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;

    public <T> FigurePropertyChangeEvent(Figure source, Key<T> key, T oldValue, T newValue) {
        super(source);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Key<?> getKey() {
        return key;
    }

    public <T> T getOldValue() {
        @SuppressWarnings("unchecked") T oldValue = (T) this.oldValue;
        return oldValue;
    }

    public <T> T getNewValue() {
        @SuppressWarnings("unchecked") T newValue = (T) this.newValue;
        return newValue;
    }

}
