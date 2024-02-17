/* @(#)ODGAttributedFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.odg.figures;

import org.jhotdraw.draw.AbstractAttributedFigure;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.samples.odg.ODGAttributeKeys;
import org.jhotdraw.samples.odg.ODGConstants;
import org.jhotdraw.samples.odg.ODGLabels;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;

import static org.jhotdraw.samples.odg.ODGAttributeKeys.FILL_STYLE;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.OPACITY;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.STROKE_STYLE;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.TRANSFORM;
/**
 * ODGAttributedFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class ODGAttributedFigure extends AbstractAttributedFigure implements ODGFigure {
    private static final long serialVersionUID = 1L;
    
    /** Creates a new instance. */
    public ODGAttributedFigure() {
    }
    
    @Override
    public void draw(Graphics2D g)  {
        double opacity = get(OPACITY);
        opacity = Math.min(Math.max(0d, opacity), 1d);
        if (opacity != 0d) {
            if (opacity != 1d) {
                Rectangle2D.Double drawingArea = getDrawingArea();
                
                Rectangle2D clipBounds = g.getClipBounds();
                if (clipBounds != null) {
                    Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
                }
                
                if (! drawingArea.isEmpty()) {
                    
                    BufferedImage buf = new BufferedImage(
                            (int) ((2 + drawingArea.width) * g.getTransform().getScaleX()),
                            (int) ((2 + drawingArea.height) * g.getTransform().getScaleY()),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gr = buf.createGraphics();
                    gr.scale(g.getTransform().getScaleX(), g.getTransform().getScaleY());
                    gr.translate((int) -drawingArea.x, (int) -drawingArea.y);
                    gr.setRenderingHints(g.getRenderingHints());
                    drawFigure(gr);
                    gr.dispose();
                    Composite savedComposite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
                    g.drawImage(buf, (int) drawingArea.x, (int) drawingArea.y,
                            2 + (int) drawingArea.width, 2 + (int) drawingArea.height, null);
                    g.setComposite(savedComposite);
                }
            } else {
                drawFigure(g);
            }
        }
    }
    
    /**
     * This method is invoked before the rendered image of the figure is
     * composited.
     */
    public void drawFigure(Graphics2D g) {
        AffineTransform savedTransform = null;
        if (get(TRANSFORM) != null) {
            savedTransform = g.getTransform();
            g.transform(get(TRANSFORM));
        }
        
        if (get(FILL_STYLE) != ODGConstants.FillStyle.NONE) {
            Paint paint = ODGAttributeKeys.getFillPaint(this);
            if (paint != null) {
                g.setPaint(paint);
                drawFill(g);
            }
        }
        
        if (get(STROKE_STYLE) != ODGConstants.StrokeStyle.NONE) {
            Paint paint = ODGAttributeKeys.getStrokePaint(this);
            if (paint != null) {
                g.setPaint(paint);
                g.setStroke(ODGAttributeKeys.getStroke(this));
                drawStroke(g);
            }
        }
        if (get(TRANSFORM) != null) {
            g.setTransform(savedTransform);
        }
    }
    @Override
    public <T> void set(AttributeKey<T> key, T newValue) {
        if (key == TRANSFORM) {
            invalidate();
        }
        super.set(key, newValue);
    }
    @Override public Collection<Action> getActions(Point2D.Double p) {
        LinkedList<Action> actions = new LinkedList<Action>();
        if (get(TRANSFORM) != null) {
            ResourceBundleUtil labels = ODGLabels.getLabels();
            actions.add(new AbstractAction(labels.getString("edit.removeTransform.text")) {
    private static final long serialVersionUID = 1L;
                public void actionPerformed(ActionEvent evt) {
                    willChange();
                    fireUndoableEditHappened(
                            TRANSFORM.setUndoable(ODGAttributedFigure.this, null)
                            );
                    changed();
                }
            });
        }
        return actions;
    }
}
