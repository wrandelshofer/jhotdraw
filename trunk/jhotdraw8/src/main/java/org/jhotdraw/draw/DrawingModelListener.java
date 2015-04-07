/* @(#)DrawingModelListener.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

/**
 * DrawingModelListener.
 * @author Werner Randelshofer
 * @version $Id$
 */
@FunctionalInterface
public interface DrawingModelListener {

    void handle(DrawingModelEvent mutation);
}
