/* @(#)ToolAdapter.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.draw.event;

import javax.annotation.Nonnull;

/**
 * An abstract adapter class for receiving {@link ToolEvent}s. This class
 * exists as a convenience for creating {@link ToolListener} objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToolAdapter implements ToolListener {

    @Override
    public void toolStarted(ToolEvent event) {
    }

    @Override
    public void toolDone(ToolEvent event) {
    }

    @Override
    public void areaInvalidated(ToolEvent e) {
    }

    @Override
    public void boundsInvalidated(ToolEvent e) {
    }

}
