/* @(#)Listener.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.event;

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
    /** Handles an event. */
    void handle(E event);
}
