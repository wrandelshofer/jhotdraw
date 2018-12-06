/* @(#)Liner.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.liner;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.BezierPath;

import java.util.Collection;

/**
 * A <em>liner</em> encapsulates a strategy for laying out the bezier path of
 * a {@link ConnectionFigure}.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * The control points of the bezier path of a connection figure can be laid out
 * using different layout algorithms which are implemented by liners.<br>
 * Context: {@link ConnectionFigure}; Strategy: {@link Liner}.
 * <hr>
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Liner extends Cloneable {
    
    /**
     * Layouts the Path. This may alter the number and type of points
     * in the Path.
     *
     * @param figure The ConnectionFigure to be lined out.
     */
    public void lineout(ConnectionFigure figure);
    
    /**
     * Creates Handle's for the Liner.
     * The ConnectionFigure can provide these handles to the user, in order
     * to let her control the lineout.
     * 
     * @param path The path for which to create handles.
     */
    public Collection<Handle> createHandles(BezierPath path);
    
    public Liner clone();
}
