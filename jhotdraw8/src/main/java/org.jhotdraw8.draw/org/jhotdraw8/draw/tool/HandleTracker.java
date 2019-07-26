/*
 * @(#)HandleTracker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.handle.Handle;

import java.util.Collection;

/**
 * A <em>handle tracker</em> provides the behavior for manipulating a
 * {@link Handle} of a figure to the {@link SelectionTool}.
 *
 * @author Werner Randelshofer
 * @design.pattern SelectionTool Strategy, Strategy.
 * @design.pattern HandleTracker Chain of Responsibility, Handler. Mouse and
 * keyboard events occur on a {@link org.jhotdraw8.draw.DrawingView}, and are
 * preprocessed by {@link SelectionTool}, and then by {@link HandleTracker}.
 * {@code HandleTracker} invokes corresponding methods on a {@link Handle} which
 * in turn changes an aspect of a figure.
 */
public interface HandleTracker extends Tracker {

    public void setHandles(@Nullable Handle handle, Collection<Figure> compatibleFigures);

}
