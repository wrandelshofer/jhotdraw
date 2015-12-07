/* @(#)AbstractConnector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.figure.Figure;

/**
 * AbstractConnector.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractConnector implements Connector {

    protected final Figure target;

    public AbstractConnector(Figure target) {
        this.target = target;
    }

    @Override
    public Figure getTarget() {
        return target;
    }

}
