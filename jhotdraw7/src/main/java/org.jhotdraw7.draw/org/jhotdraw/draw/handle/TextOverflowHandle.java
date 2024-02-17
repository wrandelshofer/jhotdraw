/* @(#)TextOverflowHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.handle;

import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TextHolderFigure;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;

/**
 * The TextOverflowHandle indicates when the text does not fit into the
 * bounds of a TextAreaFigure.
 *
 * @author Werner Randelshofer
 * @version $Id: TextOverflowHandle.java -1   $
 */
public class TextOverflowHandle extends AbstractHandle {
    
    /** Creates a new instance. */
    public TextOverflowHandle(TextHolderFigure owner) {
        super(owner);
    }
    
    @Override
    public TextHolderFigure getOwner() {
        return (TextHolderFigure) super.getOwner();
    }
    @Override
    public boolean contains(Point p) {
        return false;
    }
    
    /**
     * Draws this handle.
     */
    @Override public void draw(Graphics2D g) {
        if (getOwner().isTextOverflow()) {
        drawRectangle(g, 
                getEditor().getHandleAttribute(HandleAttributeKeys.OVERFLOW_HANDLE_FILL_COLOR),
                getEditor().getHandleAttribute(HandleAttributeKeys.OVERFLOW_HANDLE_STROKE_COLOR)
                );
            g.setColor(getEditor().getHandleAttribute(HandleAttributeKeys.OVERFLOW_HANDLE_STROKE_COLOR));
            Rectangle r = basicGetBounds();
            g.drawLine(r.x+1, r.y+1, r.x+r.width-2, r.y+r.height-2);
            g.drawLine(r.x+r.width-2, r.y+1, r.x+1, r.y+r.height-2);
        }
    }
    
    @Override protected Rectangle basicGetBounds() {
        Rectangle2D.Double b = getOwner().getBounds();
        Point2D.Double p = new Point2D.Double(b.x + b.width, b.y  + b.height);
        Figure o = getOwner();
        if (o.get(TRANSFORM) != null) {
            o.get(TRANSFORM).transform(p, p);
        }
        Rectangle r = new Rectangle(view.drawingToView(p));
        int h = getHandlesize();
        r.x -= h;
        r.y -= h;
        r.width = r.height = h;
        return r;
    }
    
    @Override
    public void trackStart(Point anchor, int modifiersEx) {
    }
    
    @Override
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
    }
    
    @Override
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    }
    
    @Override public String getToolTipText(Point p) {
        
        return (getOwner().isTextOverflow()) ?
            DrawLabels.getLabels().//
            getString("handle.textOverflow.toolTipText") :
            null;
    }
    
}
