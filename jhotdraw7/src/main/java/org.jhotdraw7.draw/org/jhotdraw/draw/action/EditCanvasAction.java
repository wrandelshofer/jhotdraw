/* @(#)EditCanvasAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

/**
 * EditCanvasAction.
 * <p>
 * XXX - We shouldn't have a dependency to the application framework
 * from within the drawing framework.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EditCanvasAction extends AbstractDrawingViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "view.editCanvas";
    private JFrame frame;
    private EditCanvasPanel settingsPanel;
    private PropertyChangeListener propertyChangeHandler;
    private Application app;
    
    /** Creates a new instance. */
    public EditCanvasAction(Application app, DrawingEditor editor) {
        super(editor);
        this.app = app;
        ResourceBundleUtil labels = DrawLabels.getLabels();
        labels.configureAction(this, ID);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        getFrame().setVisible(true);
    }
    
   @Override protected void updateViewState() {
        if (getView() != null && settingsPanel != null) {
            settingsPanel.setDrawing(getView().getDrawing());
        }
    }
    
    protected Application getApplication() {
        return app;
    }
    
    protected JFrame getFrame() {
        if (frame == null) {
            ResourceBundleUtil labels = DrawLabels.getLabels();
            frame = new JFrame();
            frame.setTitle(labels.getString("window.editCanvas.title"));
            frame.setResizable(false);
            settingsPanel = new EditCanvasPanel();
            frame.add(settingsPanel);
            frame.pack();
            Preferences prefs = PreferencesUtil.userNodeForPackage(getClass());
            PreferencesUtil.installFramePrefsHandler(prefs, "canvasSettings", frame);
            getApplication().addWindow(frame, null);
        }
            settingsPanel.setDrawing(getView().getDrawing());
        return frame;
    }
}
