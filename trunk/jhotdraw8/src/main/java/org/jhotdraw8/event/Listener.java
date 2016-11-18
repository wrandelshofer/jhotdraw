/* @(#)Listener.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.event;

import java.util.EventObject;

/**
 * Functional listener interface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <E> The event type
 */
@FunctionalInterface
public interface Listener<E extends EventObject> {
    /** Handles an event.
     * @param event the event */
    void handle(E event);
}
