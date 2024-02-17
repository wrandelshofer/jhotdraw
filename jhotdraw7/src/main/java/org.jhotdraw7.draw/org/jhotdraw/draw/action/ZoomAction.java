/* @(#)ZoomAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;

/**
 * ZoomAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ZoomAction extends AbstractDrawingViewAction {
    private static final long serialVersionUID = 1L;
    private double scaleFactor;
    @Nullable private AbstractButton button;
    private String label;
    /**
     * Creates a new instance.
     */
    public ZoomAction(@Nullable DrawingEditor editor, double scaleFactor, @Nullable AbstractButton button) {
        this((DrawingView) null, scaleFactor, button);
        setEditor(editor);
    }
    /**
     * Creates a new instance.
     */
    public ZoomAction(@Nullable DrawingView view, double scaleFactor, @Nullable AbstractButton button) {
        super(view);
        this.scaleFactor = scaleFactor;
        this.button = button;
        label = (int) (scaleFactor * 100)+" %";
        putValue(Action.DEFAULT, label);
        putValue(Action.NAME, label);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (button != null) {
            button.setText(label);
        }
        final Rectangle vRect = getView().getComponent().getVisibleRect();
        final double oldFactor = getView().getScaleFactor();
        getView().setScaleFactor(scaleFactor);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (vRect != null) {
                    vRect.x = (int) (vRect.x / oldFactor * scaleFactor);
                    vRect.y = (int) (vRect.y / oldFactor * scaleFactor);
                    vRect.width = (int) (vRect.width / oldFactor * scaleFactor);
                    vRect.height = (int) (vRect.height / oldFactor * scaleFactor);
                    vRect.x += vRect.width / 3;
                    vRect.y += vRect.height / 3;
                    vRect.width /= 3;
                    vRect.height /= 3;
                    getView().getComponent().scrollRectToVisible(vRect);
                }
            }
        });
    }
    
}
