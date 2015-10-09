/* @(#)HandleTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.Collection;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;

/**
 * A <em>handle tracker</em> provides the behavior for manipulating a
 * {@link Handle} of a figure to the {@link SelectionTool}.
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * The different behavior states of the selection tool are implemented by
 * trackers.<br>
 * Context: {@link SelectionTool}; State: {@link DragTracker},
 * {@link HandleTracker}, {@link SelectAreaTracker}.
 * <hr>
 *
 * XXX Do we really need this class? Handle can process input events on its own! 

 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HandleTracker extends Tracker {

    public void setHandles(Handle handle, Collection<Figure> compatibleFigures);

}
