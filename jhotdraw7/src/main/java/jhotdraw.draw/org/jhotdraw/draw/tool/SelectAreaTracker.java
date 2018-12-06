/* @(#)SelectAreaTracker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.tool;

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
public interface SelectAreaTracker extends Tool {

}
