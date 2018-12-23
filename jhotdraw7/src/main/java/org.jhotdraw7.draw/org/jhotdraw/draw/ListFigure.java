/* @(#)ListFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw;

import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.geom.Insets2D;

import org.jhotdraw.annotation.Nullable;

/**
 * A ListFigure consists of a list of Figures and a RectangleFigure.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ListFigure
extends GraphicalCompositeFigure {
    private static final long serialVersionUID = 1L;
    /** Creates a new instance. */
    public ListFigure() {
        this(null);
    }
    /** Creates a new instance with the specified presentation figure
     * and layout insets of [top=4,left=8,right=4,bottom=8]. */
    public ListFigure(@Nullable Figure presentationFigure) {
        super(presentationFigure); 
        setLayouter(new VerticalLayouter());
        set(LAYOUT_INSETS, new Insets2D.Double(4,8,4,8));
    }
}
