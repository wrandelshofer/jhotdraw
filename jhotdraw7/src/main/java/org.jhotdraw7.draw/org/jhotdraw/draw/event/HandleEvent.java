/* @(#)HandleEvent.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.event;

import org.jhotdraw.draw.handle.Handle;

import org.jhotdraw.annotation.Nullable;
import java.awt.Rectangle;
import java.util.EventObject;
/**
 * An {@code EventObject} sent to {@link HandleListener}s.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * State changes of handles can be observed by other objects. Specifically
 * {@code DrawingView} observes area invalidations and remove requests of
 * handles.<br>
 * Subject: {@link Handle}; Observer: {@link HandleListener}; Event:
 * {@link HandleEvent}; Concrete Observer: {@link DrawingView}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HandleEvent extends EventObject {
    @Nullable private Rectangle invalidatedArea;
    private static final long serialVersionUID=1L;
    
    /** Creates a new instance. */
    public HandleEvent(Handle src, @Nullable Rectangle invalidatedArea) {
        super(src);
        this.invalidatedArea = invalidatedArea;
    }
    
    public Handle getHandle() {
        return (Handle) getSource();
    }
    /**
     *  Gets the bounds of the invalidated area on the drawing view.
     */
    @Nullable public Rectangle getInvalidatedArea() {
        return invalidatedArea;
    }
}
