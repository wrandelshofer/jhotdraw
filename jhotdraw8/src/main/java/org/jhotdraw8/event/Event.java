/* @(#)Event.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.event;

import javax.annotation.Nonnull;

import java.util.EventObject;

/**
 * Event.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Event<E> extends EventObject {

    private final static long serialVersionUID = 1L;

    public Event(@Nonnull E source) {
        super(source);
    }

    @Nonnull
    @Override
    public E getSource() {
        @SuppressWarnings("unchecked")
        E temp = (E) super.getSource();
        return temp;
    }

}
