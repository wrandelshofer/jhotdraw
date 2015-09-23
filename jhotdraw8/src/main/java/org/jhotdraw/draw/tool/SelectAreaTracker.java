/* @(#)SelectAreaTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;

/**
 * A <em>select area tracker</em> provides the behavior for selecting figures
 * in a drawing area to the {@link SelectionTool}.
 *
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
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface SelectAreaTracker extends Tracker {

}
