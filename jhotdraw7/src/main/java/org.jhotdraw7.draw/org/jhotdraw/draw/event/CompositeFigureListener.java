/* @(#)CompositeFigureListener.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */


package org.jhotdraw.draw.event;

import java.util.EventListener;

/**
 * Interface implemented by observers of {@link org.jhotdraw.draw.CompositeFigure}.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * Changes in the composition of a composite figure can be observed.<br>
 * Subject: {@link org.jhotdraw.draw.CompositeFigure}; Observer:
 * {@link CompositeFigureListener}; Event: {@link CompositeFigureEvent}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CompositeFigureListener extends EventListener {
    /**
     * Sent when a figure was added.
     */
    public void figureAdded(CompositeFigureEvent e);
    
    /**
     * Sent when a figure was removed.
     */
    public void figureRemoved(CompositeFigureEvent e);
}
