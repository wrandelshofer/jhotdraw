/* @(#)DragTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.tool;

import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A <em>drag tracker</em> provides the behavior for dragging selected
 * figures to the {@link SelectionTool}.
 *
 * @design.pattern SelectionTool Strategy, Strategy.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DragTracker extends Tracker {

    void setDraggedFigure(Figure f, DrawingView dv);


}
