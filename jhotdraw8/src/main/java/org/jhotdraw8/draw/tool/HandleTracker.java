/* @(#)HandleTracker.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.handle.Handle;

/**
 * A <em>handle tracker</em> provides the behavior for manipulating a
 * {@link Handle} of a figure to the {@link SelectionTool}.
 *
 * @design.pattern SelectionTool Strategy, Strategy.
 *
 * @design.pattern HandleTracker Chain of Responsibility, Handler. Mouse and
 * keyboard events occur on a {@link org.jhotdraw8.draw.DrawingView}, and are
 * preprocessed by {@link SelectionTool}, and then by {@link HandleTracker}.
 * {@code HandleTracker} invokes corresponding methods on a {@link Handle} which
 * in turn changes an aspect of a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HandleTracker extends Tracker {

    public void setHandles(@Nullable Handle handle,  Collection<Figure> compatibleFigures);

}
