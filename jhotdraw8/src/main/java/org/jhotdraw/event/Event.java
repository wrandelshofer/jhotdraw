/* @(#)Event.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.event;

import java.util.EventObject;

/**
 * Event.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Event<E> extends EventObject {

    public Event(E source) {
        super(source);
    }

    @Override
    public E getSource() {
        return (E)super.getSource(); 
    }

}
