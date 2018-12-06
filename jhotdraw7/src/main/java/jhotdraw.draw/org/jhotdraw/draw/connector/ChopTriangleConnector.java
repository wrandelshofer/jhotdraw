/* @(#)ChopTriangleConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TriangleFigure;

import java.awt.geom.Point2D;

/**
 * A {@link Connector} which locates a connection point at the bounds
 * of a {@link TriangleFigure}.
 * <p>
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class ChopTriangleConnector extends ChopRectangleConnector {
    private static final long serialVersionUID = 1L;
    
    /**
     * Only used for DOMStorable input.
     */
    public ChopTriangleConnector() {
    }
    /** Creates a new instance. */
    public ChopTriangleConnector(TriangleFigure owner) {
        super(owner);
    }
    
    @Override
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        TriangleFigure bf = (TriangleFigure) getConnectorTarget(target);
        return bf.chop(from);
    }
}
