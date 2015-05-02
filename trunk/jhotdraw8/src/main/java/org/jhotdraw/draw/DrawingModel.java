/* @(#)DrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.jhotdraw.event.Listener;

/**
 * DrawingModel allows to observe all changes for a tree of figures.
 * <p>
 * DrawingModel is typically used with a Drawing object as the root of the tree.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingModel extends Observable {

    void addListener(Listener<DrawingModelEvent> listener);

    void removeListener(Listener<DrawingModelEvent> listener);
    
    /** Sets the root of the drawing model. 
     * @param newValue the root. Specify null to unlink the drawing model.
     */
    void setRoot(Figure newValue);

}
