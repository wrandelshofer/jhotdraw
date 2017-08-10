/*
 * @(#)HandleTracker.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.handle.Handle;
import java.util.Collection;

/**
 * A <em>handle tracker</em> provides the behavior for manipulating a
 * {@link Handle} of a figure to the {@link SelectionTool}.
 * 
 * @design.pattern HandleTracker Chain of Responsibility, Handler. 
 * The different behavior states of the selection tool are implemented by
 * trackers.  Context: {@link SelectionTool}; State: {@link DragTracker},
 * {@link HandleTracker}, {@link SelectAreaTracker}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HandleTracker extends Tool {

    public void setHandles(Handle handle, Collection<Handle> compatibleHandles);

}
