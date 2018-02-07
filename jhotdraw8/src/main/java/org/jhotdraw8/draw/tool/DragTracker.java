/* @(#)DragTracker.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A <em>drag tracker</em> provides the behavior for dragging selected figures
 * to the {@link SelectionTool}.
 *
 * @design.pattern SelectionTool Strategy, Strategy.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DragTracker extends Tracker {

    void setDraggedFigure(@Nullable Figure f,  DrawingView dv);

}
