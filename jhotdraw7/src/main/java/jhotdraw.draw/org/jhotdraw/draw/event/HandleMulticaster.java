/* @(#)HandleMulticaster.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.util.ReversedList;

import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Forwards events to one or many handles.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HandleMulticaster {

    LinkedList<Handle> handles;

    /** Creates a new instance. */
    public HandleMulticaster(Handle handle) {
        this.handles = new LinkedList<Handle>();
        this.handles.add(handle);
    }

    /** Creates a new instance. */
    public HandleMulticaster(Collection<Handle> handles) {
        this.handles = new LinkedList<Handle>(handles);
    }

    public void draw(java.awt.Graphics2D g) {
        for (Handle h : handles) {
            h.draw(g);
        }
    }

    public void keyPressed(java.awt.event.KeyEvent e) {
        for (Handle h : handles) {
            h.keyPressed(e);
            if (e.isConsumed()) {
                break;
            }
        }
    }

    public void keyReleased(java.awt.event.KeyEvent e) {
        for (Handle h : handles) {
            h.keyReleased(e);
        }
    }

    public void keyTyped(java.awt.event.KeyEvent e) {
        for (Handle h : handles) {
            h.keyTyped(e);
        }
    }

    public void trackEnd(Point current, Point anchor, int modifiersEx, DrawingView view) {
        for (Handle h : new ReversedList<Handle>(handles)) {
            h.trackEnd(current, anchor, modifiersEx);
        }
    }

    public void trackStart(Point anchor, int modifiersEx, DrawingView view) {
        for (Handle h : handles) {
            h.trackStart(anchor, modifiersEx);
        }
    }

    public void trackDoubleClick(Point p, int modifiersEx, DrawingView view) {
        for (Handle h : handles) {
            h.trackDoubleClick(p, modifiersEx);
        }
    }

    public void trackStep(Point anchor, Point lead, int modifiersEx, DrawingView view) {
        for (Handle h : handles) {
            h.trackStep(anchor, lead, modifiersEx);
        }
    }
}
