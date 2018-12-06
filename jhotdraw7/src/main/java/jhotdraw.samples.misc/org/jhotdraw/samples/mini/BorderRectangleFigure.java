/* @(#)BorderRectangle2D.DoubleFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.mini;

import org.jhotdraw.draw.RectangleFigure;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * BorderRectangle2D.DoubleFigure.
 *
 * @deprecated This class should be in one of the samples package
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
@Deprecated
public class BorderRectangleFigure extends RectangleFigure {
    private static final long serialVersionUID = 1L;
    protected Border border;
    protected static final JComponent borderComponent = new JPanel();
    
    /** Creates a new instance. */
    public BorderRectangleFigure(Border border) {
        this.border = border;
    }
    
    public void drawFigure(Graphics2D g) {
        Rectangle bounds = getBounds().getBounds();
        border.paintBorder(borderComponent, g, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
