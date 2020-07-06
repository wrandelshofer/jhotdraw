/*
 * @(#)Listener.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.event;

import java.util.EventObject;

/**
 * Functional listener interface.
 *
 * @author Werner Randelshofer
 */
@FunctionalInterface
public interface Listener<E extends EventObject> {

    /**
     * Handles an event.
     *
     * @param event the event
     */
    void handle(E event);
}
