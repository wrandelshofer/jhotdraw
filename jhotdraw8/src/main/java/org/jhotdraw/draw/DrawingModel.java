/* @(#)DrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw;

/**
 * DrawingModel allows to observe all changes for a tree of figures.
 * <p>
 * DrawingModel is typically used with a Drawing object as the root of the tree.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingModel {

    void addListener(DrawingModelListener listener);

    void removeListener(DrawingModelListener listener);

    void setRoot(Figure newValue);

}
